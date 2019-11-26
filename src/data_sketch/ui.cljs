(ns data-sketch.ui)

;; SIZE

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

;; parsing

(defn absolute-value-size-expr [expr]
  (let [[value unit] expr]
    (if (and (= (count expr) 2)
             (number? value)
             (or (= unit :px) (= unit :part)))
      {:type unit :value value}
      nil)))

(defn relative-value-size-expr [expr]
  (cond
    (= expr :fill) {:type :fill}
    (= expr :shrink) {:type :shrink}
    :else nil))


(defn value-size-expr [expr]
  (or
    (relative-value-size-expr expr)
    (absolute-value-size-expr expr)))


(defn constraint-size-expr [expr]
  (let [[constraint-type value-expr] expr
        value (absolute-value-size-expr value-expr)]
    (if (and (not (nil? value))
             (= (count expr) 2))
      (case constraint-type
        :min {:min value}
        :max {:max value}
        nil))))

(defn list-size-expr [expr]
  (reduce (fn [agg sub-expr]
            (if-let [result (or
                              (value-size-expr sub-expr)
                              (constraint-size-expr sub-expr))]
              (merge agg result)
              agg))
          {}
          expr))

(defn size-expr [expr]
  (or
    (value-size-expr expr)
    (constraint-size-expr expr)
    (list-size-expr expr)))

(size-expr nil)
(size-expr :fill)
(size-expr :shrink)
(size-expr [20 :px])
;;(size-expr ["50" :px])
(size-expr [2 :part])
(size-expr [:min [100 :px]])
(size-expr [[1 :part] [:min [100 :px]] [:max [200 :px]]])


;; ELEMENTS

(defn attrs->style [attrs]
  (merge
    (size->style "width" (size-expr (:width attrs)))
    (size->style "height" (size-expr (:height attrs)))))

(defn transform-attrs [attrs]
  (merge
    (dissoc attrs :width :height)
    {:style (attrs->style attrs)}))

(defn box [attrs]
  [:div.ui-box (transform-attrs attrs)])

(defn row [attrs & children]
  (print "transformed attr" (transform-attrs attrs) attrs)
  [:div.ui-row (transform-attrs attrs) children])

(defn column [attrs & children]
  [:div.ui-column (transform-attrs attrs) children])

(defn button [attrs & children]
  [:button.ui-button (transform-attrs attrs) children])

(defn text [attrs value]
  [:span.ui-text (transform-attrs attrs) value])

(defn paragraph [attrs value]
  [:p.ui-paragraph (transform-attrs attrs) value])
