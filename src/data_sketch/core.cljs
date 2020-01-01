(ns data-sketch.core
  (:require [datascript.core :as d]
            [data-sketch.db :as db :refer [conn]]
            [reagent.core :as r]
            [posh.reagent :as p]))


;; ACTIONS

(defn create-query [conn kind-id]
  (p/transact! conn [{:ds.query/kind kind-id}]))

(defn add-column [conn query-id attribute-id]
  (p/transact! conn [[:db/add query-id :ds.query/column attribute-id]]))

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
            :on-click #(create-query conn kind-id)}
           name])
        kinds)]]))

(defn attribute-option [conn query-id attribute-id]
  (let [{attribute-key :db/ident} @(p/pull conn '[:db/ident] attribute-id)
        attribute-column-ids (-> @(p/q '[:find ?attribute-id
                                         :in $ ?query-id ?attribute-id
                                         :where
                                         [?query-id :ds.query/column ?attribute-id]] conn query-id attribute-id)
                                 first)
        is-selected (not (empty? attribute-column-ids))]
    [:tr
     [:td [:label.FactOptions__Option
           [:input {:type      "checkbox"
                    :checked   is-selected
                    :on-change #(if is-selected
                                  (retract-entities conn attribute-column-ids)
                                  (add-column conn query-id attribute-id))}]
           [:label.FactOptions__OptionName (name attribute-key)]]]
     [:td [:label.FactOptions__ExampleValue ""]]]))

(defn column-header [conn column-id]
  (let [column @(p/pull conn '[:db/ident] column-id)
        column-name (-> column :db/ident name)]
    [:th.Entity__HeaderLabel column-name]))


(defn query-result [conn query-id]
  (let [query @(p/pull conn '[:ds.query/column] query-id)
        column-ids (map :db/id (:ds.query/column query))]

    (if (= (count column-ids) 0)
      [:tbody.Entity__Result]

      (let [columns @(p/pull-many conn '[:db/ident] column-ids)

            variables (map (fn [{attribute-name :db/ident}]
                             {:name           (symbol (str "?" (name attribute-name)))
                              :attribute-name attribute-name}) columns)

            query (-> '(:find ?entity)
                      (concat (map :name variables))
                      (concat '(:in $))
                      (concat '(:where))
                      (concat (map (fn [variable]
                                     ['?entity (:attribute-name variable) (:name variable)])
                                   variables))
                      (vec))

            rows (map rest @(p/q query conn))]

        (print query)
        [:tbody.Entity__Result
         (map-indexed
           (fn [i row]
             ^{:key i} [:tr
                        (map-indexed
                          (fn [j value]
                            ^{:key j} [:td {:title value} value])
                          row)])
           rows)]))))


(defn query-view [conn query-id kind-id]
  (let [kind @(p/pull conn '[:ds.kind/name :ds.kind/attribute] kind-id)
        attribute-ids (map :db/id (:ds.kind/attribute kind))
        query @(p/pull conn '[:ds.query/column] query-id)
        column-ids (map :db/id (:ds.query/column query))]
    [:table.Entity
     [:thead
      [:tr
       [:th {:col-span (count column-ids)}
        [:div.Entity__HeaderLabel (:ds.kind/name kind)]

        [:table.FactOptions
         [:tbody
          (map (fn [attribute-id]
                 ^{:key attribute-id} [attribute-option conn query-id attribute-id])
               attribute-ids)]]]]
      [:tr
       (map (fn [column-id]
              ^{:key column-id} [column-header conn column-id]) column-ids)]]
     [query-result conn query-id]]))

(defn queries-view [conn]
  (let [queries @(p/q '[:find ?query ?kind
                        :in $
                        :where [?query :ds.query/kind ?kind]] conn)]
    [:<> (map
           (fn [[query-id kind-id]]
             ^{:key query-id} [query-view conn query-id kind-id])
           queries)]))

(defn app [conn]
  [:div.Content

   [:h1 "Movies example"]

   [queries-view conn]

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

