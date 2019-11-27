(ns data-sketch.ui
  (:require [react :as React]
            [react-dom :as ReactDOM]
            [clojure.string :as str]))


;; SIZE

(defn normalize-px-value [px-value]
  (if (vector? px-value)
    (let [[value unit] px-value]
      (if (and (number? value)
               (= unit :px))
        value
        nil))
    nil))

(defn normalize-size-value [value]
  (cond
    (= value :fill) {:type :fill}
    (= value :shrink) {:type :shrink}
    (vector? value) (let [[v unit] value]
                      (if (and (number? v)
                               (or (= :px unit) (= :fraction unit)))
                        {:type unit :value v}
                        nil))
    ))

(comment
  (normalize-size-value [1 :px])
  (normalize-size-value [20 :fraction])
  (normalize-size-value [10 :foobar])
  (normalize-size-value nil)
  (normalize-size-value :fill)
  (normalize-size-value {:value [20 :px]}))


(defn normalize-complex-size [{:keys [value min max]}]
  {:value (normalize-px-value value)
   :min   (normalize-px-value min)
   :max   (normalize-px-value max)})

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

(defn size->style [dimension size]
  (reduce-kv (fn [style key value]
               (case key
                 :value (case (:type size)
                          :shrink (assoc style :display "inline-block")

                          :fill (assoc style (keyword dimension) "100%")

                          :px (assoc style
                                (keyword dimension) (str value "px")
                                :flex-shrink 0
                                :flex-grow 0)

                          :part (assoc style
                                  :flex-shrink 1
                                  :flex-grow value)

                          style)

                 :min (assoc style
                        (keyword (str "min-" dimension))
                        (str (:value value) "px"))

                 :max (assoc style
                        (keyword (str "max-" dimension))
                        (str (:value value) "px"))


                 style))
             {}
             size))


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

(defn overflow->style [{:keys [x y]}]
  (if (and (= x y) (not (nil? x)))
    {:overflow (name x)}
    (cond-> {}
            (not (nil? x)) (assoc :overflow-x (name x))
            (not (nil? y)) (assoc :overflow-y (name y)))))

(comment
  (overflow->style {:y :clip :x :overflow})
  (overflow->style {:y :clip})
  (overflow->style {:y :clip :x :clip}))


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


(defn spacing->style [{:keys [padding evenSpacing gap]}]
  (if evenSpacing
    {:align-items     "center"
     :justify-content "space-evenly"}
    ()))

(defn padding->style [{:keys [top right bottom left]}]
  {:padding (str top "px " right "px " bottom "px " left "px")})


;; ELEMENTS

(defn attrs->style [attrs]
  (merge
    {}
    #_(size->style "width" (size-value (:width attrs)))
    #_(size->style "height" (size-value (:height attrs)))))

(defn transform-attrs [attrs]
  (merge
    (dissoc attrs :width :height)
    #_{:style (attrs->style attrs)}))


(defn box [attrs & children]
  {:tag      "div"
   :attrs    {:className "ui-box"}
   :children (flatten children)})

(defn row [attrs & children]
  {:tag      "div"
   :attrs    {:className "ui-row"}
   :children (flatten children)})

(defn column [attrs & children]
  {:tag      "div"
   :attrs    {:className "ui-column"}
   :children (flatten children)})

(defn button [attrs & children]
  {:tag      "button"
   :attrs    {:className "ui-button" :onClick (:on-click attrs)}
   :children (flatten children)})

(defn text [attrs value]
  {:tag      "span"
   :attrs    {:className "ui-text"}
   :children (list value)})

(defn paragraph [attrs value]
  {:tag      "p"
   :attrs    {:className "ui-paragraph"}
   :children (list value)})

(defn element->react [element]
  (if (string? element)
    element
    (let [{:keys [attrs tag children]} element]
      (React/createElement tag (clj->js attrs) (map element->react children)))))

(defn render [element container]
  (print "render" (cljs.pprint/pprint element))
  (ReactDOM/render (element->react element) container))




