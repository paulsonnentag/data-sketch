(ns data-sketch.ui-test
  (:require [data-sketch.ui :as ui]
            [rum.core :as r]))


(print "dostuff 123")

(r/defc examples []
  [:div {}
   [:h1 {} "examples"]
   [:h2 {} "row"]

   [:p {} "default"]

   (ui/row {}
           (ui/box {})
           (ui/box {})
           (ui/box {}))

   [:p {} "fixed width"]

   (ui/row {:width [500 :px]}
           (ui/box {})
           (ui/box {})
           (ui/box {}))


   [:p {} "part unit"]

   (ui/row {:width [500 :px]}
           (ui/box {:width [1 :part]})
           (ui/box {:width [2 :part]})
           (ui/box {:width [3 :part]}))

   [:p {} "mix pixel/part"]

   (ui/row {:width [500 :px]}
           (ui/box {:width [1 :part]})
           (ui/box {:width [1 :part]})
           (ui/box {:width [100 :px]}))

   [:p {} "constraints"]

   (ui/row {:width [500 :px]}
           (ui/box {:width [[2 :part] [:max [100 :px]]]})
           (ui/box {:width [2 :part]})
           (ui/box {:width [[1 :part] [:min [200 :px]]]}))

   [:h2 {} "column"]


   (ui/column {}
              (ui/box {})
              (ui/box {})
              (ui/box {}))


   [:h2 {} "button"]

   (ui/button {} (ui/text {} "Click me"))

   [:h2 {} "text"]

   [:p {} "text"]

   (ui/text {} "some text")

   [:p {} "paragraph"]

   (ui/text {} "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")

   ])

(r/mount (examples) (.getElementById js/document "root"))
