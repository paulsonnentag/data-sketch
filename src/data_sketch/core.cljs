(ns data-sketch.core
  (:require [datascript.core :as d]
            [data-sketch.db :as db :refer [conn]]
            [reagent.core :as r]
            [posh.reagent :as p]))


;; ACTIONS

(defn create-search [conn kind-id]
  (p/transact! conn [{:db/id            -1
                      :ds.variable/kind kind-id}
                     {:ds.search/kind     kind-id
                      :ds.search/variable -1}]))

(defn add-relationship
  [conn variable-id attribute-id ref-kind]
  (let [variable (cond-> {:db/id -1}
                         (not (nil? ref-kind)) (assoc :ds.variable/kind ref-kind))

        relationship {:db/id                     -2
                      :ds.relationship/attribute attribute-id
                      :ds.relationship/from      variable-id
                      :ds.relationship/to        -1}]
    (p/transact! conn [variable relationship])))

(defn retract-entities [conn entity-ids]
  (let [retractions (map (fn [entity-id]
                           [:db/retractEntity entity-id])
                         entity-ids)]
    (p/transact! conn retractions)))


;; VIEWS

(p/posh! conn)

(defn new-search [conn]
  (let [kinds @(p/q '[:find ?kind ?name
                      :in $
                      :where [?kind :ds.kind/name ?name]] conn)]
    [:div.SearchPlaceholder
     [:div "search for ..."]

     [:div.SearchPlaceholder__Options
      (map
        (fn [[kind-id name]]
          [:button.EntityType
           {:key      kind-id
            :on-click #(create-search conn kind-id)}
           name])
        kinds)]]))

(defn attribute-option [conn variable-id attribute-id]
  (let [attribute @(p/pull conn '[:db/ident :ds/ref-kind] attribute-id)
        attribute-name (-> attribute :db/ident name)
        ref-kind-id (-> attribute :ds/ref-kind :db/id)
        existing-relationship-ids (->> @(p/q '[:find ?relationship-id
                                               :in $ ?variable-id ?attribute-id
                                               :where
                                               [?relationship-id :ds.relationship/from ?variable-id]
                                               [?relationship-id :ds.relationship/attribute ?attribute-id]]
                                             conn variable-id attribute-id)
                                       (map first))
        is-selected (not (empty? existing-relationship-ids))]
    [:tr
     [:td [:label.FactOptions__Option
           [:input {:type      "checkbox"
                    :checked   is-selected
                    :on-change #(if is-selected
                                  (retract-entities conn existing-relationship-ids)
                                  (add-relationship conn variable-id attribute-id ref-kind-id))}]
           [:label.FactOptions__OptionName attribute-name]]]
     [:td [:label.FactOptions__ExampleValue ""]]]))


(defn header-column [conn {:keys [variable-id relationship-id]}]
  (let [variable @(p/pull conn '[{:ds.variable/kind [:ds.kind/name :ds.kind/attribute]}] variable-id)
        relationship @(p/pull conn '[{:ds.relationship/attribute [:db/ident]}] relationship-id)
        kind (:ds.variable/kind variable)
        column-name (or (:ds.kind/name kind)
                        (-> relationship :ds.relationship/attribute :db/ident name))
        attribute-ids (->> kind :ds.kind/attribute (map :db/id))]
    [:th
     [:div.Entity__HeaderLabel column-name]

     [:table.FactOptions
      [:tbody
       (map (fn [attribute-id]
              ^{:key attribute-id} [attribute-option conn variable-id attribute-id]) attribute-ids)]]]))


(defn get-connected-columns [columns]
  (->> columns
       (map
         (fn [{:keys [variable-id]}]
           (let [connected-columns
                 (->> @(p/q '[:find ?connected-var ?rel
                              :in $ ?var
                              :where
                              [?rel :ds.relationship/from ?var]
                              [?rel :ds.relationship/to ?connected-var]] conn variable-id)
                      (map (fn [[var-id rel-id]]
                             {:variable-id     var-id
                              :relationship-id rel-id})))]
             connected-columns)))
       (flatten)))

(defn header-view [conn columns]
  (let [connected-columns (get-connected-columns columns)
        header-row [:tr
                    (map (fn [column]
                           ^{:key (:variable-id column)} [header-column conn column])
                         columns)]]
    (if (empty? connected-columns)
      header-row
      [:<>
       header-row
       [header-view conn connected-columns]])))


(defn search-view [conn search-id]
  (let [search @(p/pull conn '[:ds.search/variable] search-id)
        variable-id (-> search :ds.search/variable :db/id)]

    [:table.Entity
     [:thead
      [header-view conn [{:variable-id variable-id}] nil]]]))

(defn searches-view [conn]
  (let [searches @(p/q '[:find ?search
                         :in $
                         :where [?search :ds.search/variable _]] conn)]
    [:<> (map
           (fn [[search-id]]
             ^{:key search-id} [search-view conn search-id])
           searches)]))

(defn app [conn]
  [:div.Content

   [:h1 "Movies example"]

   [searches-view conn]

   [new-search conn]

   #_[:table.Entity
      [:thead
       [:tr
        [:th {:col-span 5}
         [:.Entity__HeaderLabel "movie"]

         [:table.FactOptions
          [:tbody
           [:tr
            [:td [:label.FactOptions__Option
                  [:input {:type "checkbox" :checked true}]
                  [:label.FactOptions__OptionName "name"]]]
            [:td [:label.FactOptions__ExampleValue "Monster War II - The Reckoning"]]]
           [:tr
            [:td [:label.FactOptions__Option
                  [:input {:type "checkbox" :checked true}]
                  [:label.FactOptions__OptionName "description"]]]
            [:td [:label.FactOptions__ExampleValue "The monsters are at war again and this time it is serious"]]]
           [:tr
            [:td [:label.FactOptions__Option
                  [:input {:type "checkbox" :checked true}]
                  [:label.FactOptions__OptionName "rating"]]]
            [:td [:label.FactOptions__ExampleValue 2.5]]]
           [:tr
            [:td [:label.FactOptions__Option
                  [:input {:type "checkbox" :checked true}]
                  [:label.FactOptions__OptionName "showtime"]]]
            [:td [:label.FactOptions__ExampleValue]]]]]]]

       [:tr
        [:th.Entity__HeaderLabel {:row-span 2} "name"]
        [:th.Entity__HeaderLabel {:row-span 2} "description"]
        [:th.Entity__HeaderLabel {:row-span 2} "rating"]
        [:th {:col-span 2}

         [:.Entity__HeaderLabel "showtime"]

         [:table.FactOptions
          [:tbody
           [:tr
            [:td [:label.FactOptions__Option
                  [:input {:type "checkbox" :checked true}]
                  [:label.FactOptions__OptionName "theater"]]]
            [:td [:label.FactOptions__ExampleValue "ACM"]]]
           [:tr
            [:td [:label.FactOptions__Option
                  [:input {:type "checkbox" :checked true}]
                  [:label.FactOptions__OptionName "time"]]]
            [:td [:label.FactOptions__ExampleValue "10:00"]]]]]]]
       [:tr
        [:th.Entity__HeaderLabel "theater"]
        [:th.Entity__HeaderLabel "showtime"]]]
      [:tbody.Entity__Result
       [:tr
        [:td {:row-span 3} "Monster War II - The Reckoning"]
        [:td {:row-span 3} "The monster are at war again"]
        [:td {:row-span 3} "2.5"]
        [:td "ACM"]
        [:td "10:00"]]
       [:tr
        [:td "ACM"]
        [:td "12:00"]]
       [:tr
        [:td "ACM"]
        [:td "13:00"]]]]])

;; APPLICATION SETUP

(defn start [conn]
  (r/render-component [app conn] (.getElementById js/document "root")))

(start conn)

