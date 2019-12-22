(ns data-sketch.ui-examples
  (:require [data-sketch.ui.core :as ui]))


(defn row [attrs & children]
  (ui/row (merge
            {:border  "1px dashed grey"
             :padding [5 :px]}
            attrs)
          children))

(defn box
  ([attrs]
   (box attrs "box"))
  ([attrs label]
   (ui/box (merge
             {:background "rgb(0, 125, 200)"
              :height     [50 :px]
              :width      [50 :px]
              :padding    [5 :px]}
             attrs)
           label))
  )

(defn label [label]
  (ui/box {:padding [[20 :px] [0 :px] [10 :px] [0 :px]]} label))


(def examples

  (ui/layout
    (ui/column

      (label "default row")
      (row {:gap [5 :px]}
           (box {})
           (box {})
           (box {}))

      (label "row fixed width")
      (row {:width [500 :px]}
              (box {})
              (box {})
              (box {}))

      (ui/box {} "row fraction unit")
      (ui/row {:width [500 :px] :height [50 :px]}
              (ui/box {:width [1 :part]})
              (ui/box {:width [2 :part]})
              (ui/box {:width [3 :part]}))

      (ui/box {} "row with constraints")
      (ui/row {:width [500 :px]}
              (ui/box {:width {:value [2 :part] :max [100 :px]}})
              (ui/box {:width [2 :part]})
              (ui/box {:width {:value [1 :part] :min [200 :px]}}))

      (ui/box {} "column")
      (ui/column {}
                 (ui/box {})
                 (ui/box {})
                 (ui/box {})))))

(ui/render examples (.getElementById js/document "root"))

#_(ui/render (ui/box {}) (.getElementById js/document "root"))






