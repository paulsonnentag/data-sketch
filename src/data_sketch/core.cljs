(ns data-sketch.core
  (:require [rum.core :as r]))

(enable-console-print!)

(defonce app-state (atom 0))

(r/defc counter < r/reactive []
  [:button {:on-click (fn [_] (swap! app-state inc))}
   "Clicks (" (r/react app-state) ")"])

(r/defc app []
  [:div {}
   "Hello world!!!!"
   (counter)])

(r/mount (app) (.getElementById js/document "root"))
