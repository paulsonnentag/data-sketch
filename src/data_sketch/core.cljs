(ns data-sketch.core
  (:require [clojure.string :as str]
            [datascript.core :as d]
            [data-sketch.example-data :as example-data]
            [rum.core :as rum]))

(enable-console-print!)

(def schema {:todo/tags    {:db/cardinality :db.cardinality/many}
             :todo/project {:db/valueType :db.type/ref}
             :todo/done    {:db/index true}
             :todo/due     {:db/index true}
             :tag          {:db/index true :db/cardinality :db.cardinality/many}})


(defonce conn (d/create-conn schema))

(defonce initial-data (d/transact! conn example-data/movies))

;; ACTIONS

(defn create-search []
  (let [datom {:tag "search"}]
    (d/transact! conn [datom])))

(defn delete-search [search]
  (print "delete-search" search)
  (d/transact! conn [[:db.fn/retractEntity search]]))

;; VIEWS

(rum/defc app [db]
  [:div
   [:h1 "Movies example"]

   [:.Content
    [:table.Entity
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
       [:th.Entity__HeaderLabel  "theater"]
       [:th.Entity__HeaderLabel  "showtime"]]]
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
       [:td "13:00"]]]]

    [:div.SearchPlaceholder
     [:div "search for ..."]

     [:div.SearchPlaceholder__Options
      [:button.EntityType "movie"]
      [:button.EntityType "theater"]
      [:button.EntityType "showtime"]]]]])

;; APPLICATION SETUP

(defn render [db]
  (rum/mount (app db) (.getElementById js/document "root")))

;; re-render after every db update
(defonce rerender-listener
         (d/listen! conn :render
                    (fn [transaction-report]
                      (render (:db-after transaction-report)))))

(defn datom->str [d]
  (str (if (:added d) "+" "−")
       "[" (:e d) " " (:a d) " " (pr-str (:v d)) "]"))

(defn print-transaction [tx-report]
  (let [tx-id (get-in tx-report [:tempids :db/current-tx])
        datoms (:tx-data tx-report)]
    (println
      (str/join "\n" (concat [(str "tx " tx-id ":")] (map datom->str datoms))))))

;; log all transactions
(defonce print-listener
         (d/listen! conn :log print-transaction))


;; mount application
(render @conn)
