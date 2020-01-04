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
  (let [attribute @(p/pull conn '[:db/ident :ds.ref-kind] attribute-id)
        attribute-name (-> attribute :db/ident name)
        ref-kind-id (:ds.ref-kind attribute)
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


(defn search-view [conn search-id]
  (let [search @(p/pull conn '[{:ds.search/variable [{:ds.variable/kind [*]}]}] search-id)
        variable-id (-> search :ds.search/variable :db/id)
        kind (-> search :ds.search/variable :ds.variable/kind)
        kind-name (:ds.kind/name kind)
        kind-attribute-ids (->> kind
                                :ds.kind/attribute
                                (map :db/id))]

    [:table.Entity
     [:thead
      [:tr
       [:th

        [:div.Entity__HeaderLabel kind-name]

        [:table.FactOptions
         [:tbody
          (map (fn [attribute-id]
                 ^{:key attribute-id} [attribute-option conn variable-id attribute-id])
               kind-attribute-ids)]]]]]]))

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

