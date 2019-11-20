(ns data-sketch.core
  (:require [rum.core :as r]
            [datascript.core :as d]
            [clojure.string :as str]))

(enable-console-print!)

(def schema {:todo/tags    {:db/cardinality :db.cardinality/many}
             :todo/project {:db/valueType :db.type/ref}
             :todo/done    {:db/index true}
             :todo/due     {:db/index true}
             :tag          {:db/index true :db/cardinality :db.cardinality/many}})

(def dataset [
              ; projects
              {:db/id        -1
               :tag          "project"
               :project/name "datascript"}
              {:db/id        -2
               :tag          "project"
               :project/name "nyc-webinar"}
              {:db/id        -3
               :tag          "project"
               :project/name "shopping"}

              ; todos
              {:tag          "todo"
               :todo/text    "Displaying list of todos"
               :todo/tags    ["listen" "query"]
               :todo/project -2
               :todo/done    true
               :todo/due     "2014-12-13"}
              {:tag          "todo"
               :todo/text    "Persisting to localStorage"
               :todo/tags    ["listen" "serialization" "transact"]
               :todo/project -2
               :todo/done    true
               :todo/due     "2014-12-13"}
              {:tag          "todo"
               :todo/text    "Make task completable"
               :todo/tags    ["transact" "funs"]
               :todo/project -2
               :todo/done    false
               :todo/due     "2014-12-13"}
              {:tag          "todo"
               :todo/text    "Fix fn calls on emtpy rels"
               :todo/tags    ["bug" "funs" "query"]
               :todo/project -1
               :todo/done    false
               :todo/due     "2015-01-01"}
              {:tag          "todo"
               :todo/text    "Add db filtering"
               :todo/project -1
               :todo/done    false
               :todo/due     "2015-05-30"}
              {:tag          "todo"
               :todo/text    "Soap"
               :todo/project -3
               :todo/done    false
               :todo/due     "2015-05-01"}
              {:tag          "todo"
               :todo/text    "Cake"
               :todo/done    false
               :todo/project -3}
              {:tag       "todo"
               :todo/text "Just a task" :todo/done false}
              {:tag       "todo"
               :todo/text "Another incomplete task" :todo/done false}])



(defonce conn (d/create-conn schema))

(defonce initial-data (d/transact! conn dataset))

;; ACTIONS

(defn create-search []
  (let [datom {:tag "search"}]
    (d/transact! conn [datom])))

(defn delete-search [search]
  (print "delete-search" search)
  (d/transact! conn [[:db.fn/retractEntity search]]))

;; VIEWS

(r/defc search-view [db [search]]
  [:div.search {}
   [:h2 {:key search} "search #" search
    [:button {:on-click #(delete-search search)} "delete"]]])

(r/defc searches-view [db]
  (let [searches (d/q '[:find ?e :where [?e :tag "search"]] db)]
    (map #(search-view db %1) searches)))

(r/defc app [db]
  [:div.app {}
   [:h1 {} "DataSketch"]

   (searches-view db)

   [:button {:on-click #(create-search)} "new search !!"]])

;; APPLICATION SETUP

(defn render [db]
  (r/mount (app db) (.getElementById js/document "root")))

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
