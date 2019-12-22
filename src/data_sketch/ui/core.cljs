(ns data-sketch.ui.core
  (:require [react :as React]
            [react-dom :as ReactDOM]
            [data-sketch.ui.style :as style]))

;; SIZE

(defn normalize-px-value [px-value]
  (if (vector? px-value)
    (let [[value unit] px-value]
      (if (and (number? value)
               (= unit :px))
        value
        nil))
    nil))

(comment
  (normalize-px-value [10 :px]))

(defn normalize-size-value [value]
  (cond
    (= value :fill) {:type :fill}
    (= value :shrink) {:type :shrink}
    (vector? value) (let [[v unit] value]
                      (if (and (number? v)
                               (or (= :px unit) (= :part unit)))
                        {:type unit :value v}
                        nil))
    ))

(comment
  (normalize-size-value [1 :px])
  (normalize-size-value [20 :part])
  (normalize-size-value [10 :foobar])
  (normalize-size-value nil)
  (normalize-size-value :fill)
  (normalize-size-value {:value [20 :px]}))


(defn normalize-complex-size [{:keys [value min max]}]
  {:value (normalize-size-value value)
   :min   (normalize-size-value min)
   :max   (normalize-size-value max)})

(defn normalize-size [size]
  (if-let [size-value (normalize-size-value size)]
    {:value size-value :min nil :max nil}
    (normalize-complex-size size)))

(comment
  (normalize-size :fill)
  (normalize-size :shrink)
  (normalize-size [1 :px])
  (normalize-size {:value [20 :px]})
  (normalize-size {:value [20 :px] :min 1 :max [20 :px]}))


;; OVERFLOW

(defn normalize-overflow-value [value]
  (case value
    :clip :clip
    :scroll :scroll
    :overflow :overflow
    nil))

(defn normalize-complex-overflow [{:keys [x y]}]
  {:x (normalize-overflow-value x)
   :y (normalize-overflow-value y)})

(defn normalize-overflow [value]
  (if-let [overflow-value (normalize-overflow-value value)]
    {:x overflow-value :y overflow-value}
    (normalize-complex-overflow value)))

(comment
  (normalize-overflow :clip)
  (normalize-overflow :scroll)
  (normalize-overflow :overflow)
  (normalize-overflow 1)
  (normalize-overflow {:y :clip})
  (normalize-overflow {:y :clip :x :overflow})
  (normalize-overflow {:y 2 :x 3 :z 2}))


;; SPACING

(defn normalize-complex-padding [value]
  (let [px-values (into [] (map normalize-px-value value))]
    (if (some nil? px-values)
      nil
      (case (count px-values)
        2 (let [[x y] px-values]
            {:top y :right x :bottom y :left x})
        4 (let [[top right bottom left] px-values]
            {:top top :right right :bottom bottom :left left})
        nil))))

(defn normalize-padding [value]
  (if-let [px-value (normalize-px-value value)]
    {:top    px-value
     :right  px-value
     :bottom px-value
     :left   px-value}
    (normalize-complex-padding value)))


(comment
  (normalize-padding [20 :px])
  (normalize-padding [[20 :px] [10 :px]])
  (normalize-padding [[20 :px] [20 :px] [10 :px] [10 :px]]))

(style/padding-style (normalize-padding [20 :px]))

;; ELEMENTS

(defn transform-attrs [class-name {:keys [padding gap overflow, width, height, border, background]}]
  (let [{:keys [style-sheet classes]} (style/merge-style-defs
                                        (list
                                          (style/padding-style (normalize-padding padding))
                                          (style/gap-style (normalize-gap-value gap))
                                          (style/overflow-style (normalize-overflow overflow))
                                          (style/size-style :width (normalize-size width))
                                          (style/size-style :height (normalize-size height))))
        ]
    {:attrs       {:className (str class-name (if (not (empty? classes)) " ") classes)
                   :style      (cond-> {}
                                       (not (nil? border)) (assoc :border border)
                                       (not (nil? background)) (assoc :background background))}
     :style-sheet style-sheet}))

(defn box [attrs & children]
  (let [{:keys [attrs style-sheet]} (transform-attrs style/box attrs)]
    {:tag         "div"
     :attrs       attrs
     :style-sheet style-sheet
     :children    (flatten children)}))

(comment
  (box {:width [100 :px] :padding [5 :px]}))

(defn row [attrs & children]
  (let [{:keys [attrs style-sheet]} (transform-attrs style/row attrs)]
    {:tag         "div"
     :attrs       attrs
     :style-sheet style-sheet
     :children    (flatten children)}))

(defn column [attrs & children]
  (let [{:keys [attrs style-sheet]} (transform-attrs style/column attrs)]
    {:tag         "div"
     :attrs       attrs
     :style-sheet style-sheet
     :children    (flatten children)}))

(defn button [attrs & children]
  (let [{:keys [attrs style-sheet]} (transform-attrs style/button attrs)]
    {:tag         "button"
     :attrs       attrs
     :style-sheet style-sheet
     :children    (flatten children)}))

(defn layout [content]
  {:tag      "div"
   :children (list
               content
               {:tag "style" :children (list (style/get-css content))})})

(comment
  (layout (box {:width [100 :px] :padding [5 :px]}))
  (style/get-css
    (column {:width [100 :px] :padding [5 :px]}
            (box {:width [1 :part]} "box 1")
            (box {:width [2 :part]} "box 2"))))

(defn element->react
  ([element]
   (if (string? element)
     element
     (let [{:keys [attrs tag children]} element]
       (React/createElement tag (clj->js attrs) (map-indexed element->react children)))))

  ([index element]
   (let [element-with-key (if (and (nil? (get-in element [:attrs :key]))
                                   (not (string? element)))
                            (assoc-in element [:attrs :key] index)
                            element)]
     (element->react element-with-key))))

(defn render [element container]
  (ReactDOM/render (element->react element) container))



