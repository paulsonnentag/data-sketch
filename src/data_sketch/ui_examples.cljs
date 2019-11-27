(ns data-sketch.ui-examples
  (:require [data-sketch.ui :as ui]))

(def examples
  (ui/column

    (ui/text {} "default row")
    (ui/row {}
            (ui/box {})
            (ui/box {})
            (ui/box {}))

    (ui/text {} "row fixed width")
    (ui/row {:width [500 :px]}
            (ui/box {})
            (ui/box {})
            (ui/box {}))

    (ui/text {} "row fraction unit")
    (ui/row {:width [500 :px]}
            (ui/box {:width [1 :fraction]})
            (ui/box {:width [2 :fraction]})
            (ui/box {:width [3 :fraction]}))

    (ui/text {} "row with constraints")
    (ui/row {:width [500 :px]}
            (ui/box {:width {:value [2 :part] :max [100 :px]}})
            (ui/box {:width [2 :part]})
            (ui/box {:width {:value [1 :part] :min [200 :px]}}))

    (ui/text {} "column")
    (ui/column {}
               (ui/box {})
               (ui/box {})
               (ui/box {}))))

(ui/render examples (.getElementById js/document "root"))

#_(ui/render (ui/box {}) (.getElementById js/document "root"))






