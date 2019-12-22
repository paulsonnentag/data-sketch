(ns data-sketch.core
  (:require [clojure.string :as str]
            [datascript.core :as d]
            [data-sketch.example-data :as example-data]
            [data-sketch.ui.core :as ui]))

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

(defn search-view [db [search]]
  (ui/row {}
          (ui/box {} (str "search #" search))
          (ui/button {:on-click #(delete-search search)} "delete")))

(defn searches-view [db]
  (let [searches (d/q '[:find ?e :where [?e :tag "search"]] db)]
    (ui/column {:width {:min [800 :px]}}
               (map #(search-view db %1) searches))))

(comment
  (d/q '[:find ?e :where [?e :tag "search"]] @conn))

(defn app [db]
  (ui/column {}
    (searches-view db)
    (ui/button {:on-click #(create-search)} "new search")))

;; APPLICATION SETUP

(defn render [db]
  (ui/render (app db) (.getElementById js/document "root")))

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
