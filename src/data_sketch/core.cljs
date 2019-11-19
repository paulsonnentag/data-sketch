(ns data-sketch.core
  (:require [rum.core :as r]))

(enable-console-print!)

(r/defc app []
  [:div {} "Hello world!"])

(r/mount (app) (.getElementById js/document "root"))
