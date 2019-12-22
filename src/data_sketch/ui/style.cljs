(ns data-sketch.ui.style
  (:require [clojure.string :as str]
            [clojure.set :as set]))

;; UTILS

(def empty-style-def {:style-sheet {} :classes #{}})

(defn class [name]
  (str "." name))

(defn add-style
  ([style-def name]
   (-> style-def
       (assoc-in [:classes] (conj (:classes style-def) name))))
  ([style-sheet name style]
   (-> style-sheet
       (assoc-in [:style-sheet (class name)] style)
       (add-style name))))

(comment
  (add-style {:style-sheet {} :classes #{}} "foobar" {:width "100px"}))

(defn merge-style-defs [style-defs]
  {:style-sheet (->> style-defs
                     (map :style-sheet)
                     (apply merge))

   :classes     (->> style-defs
                     (map :classes)
                     (apply set/union)
                     (str/join " "))})

(comment
  (merge-style-defs (list
                      {:style-sheet {".a" {:prop1 "A"}} :classes #{"a"}}
                      {:style-sheet {".b" {:prop1 "A"}} :classes #{"b"}}
                      {:style-sheet {".c" {:prop2 "B"}} :classes #{"a" "c"}})))

(defn properties->str [properties]
  (->> properties
       (map (fn [[key value]]
              (str (name key) ":" value)))
       (str/join "; ")))

(defn style-sheet->css [style-sheet]
  (->> style-sheet
       (map (fn [[selector properties]]
              (str selector " {" (properties->str properties) "}")))
       (str/join "\n")))

(comment
  (style-sheet->css static-style-sheet)
  (style-sheet->css {"h1" {:color "red" :padding "5px"}}))

;; ELEMENTS

(def box "ui-box")
(def row "ui-row")
(def column "ui-column")
(def button "ui-button")

;; SIZE

(def size-shrink "shr")
(def size-fill "fll")

(defn for-dimension [dimension class]
  (case dimension :width (str "w" class)
                  :height (str "h" class)))

(defn size-style [dimension {:keys [value min max]}]
  (println "size-style" value min max)
  (cond-> empty-style-def
          true (assoc :foobar :foobar)

          (not (nil? value)) (as-> style-def
                                   (case (:type value)
                                     :shrink (add-style style-def (for-dimension dimension size-shrink))

                                     :fill (add-style style-def (for-dimension dimension size-fill))

                                     :px (add-style style-def
                                                    (for-dimension dimension (str "px" (:value value)))
                                                    {dimension    (str (:value value) "px")
                                                     :flex-shrink 0
                                                     :flex-grow   0})

                                     :part (add-style style-def (for-dimension dimension (str "prt" (:value value)))
                                                      {:flex-shrink 0
                                                       :flex-grow   (str (:value value))})

                                     (println "failed value" value)))

          (not (nil? max)) (add-style
                             (for-dimension dimension (str "min" (:value max)))
                             {(case dimension
                                :width :min-width
                                :height :min-height) (str (:value max) "px")})

          (not (nil? min)) (add-style
                             (for-dimension dimension (str "max" (:value min)))
                             {(case dimension
                                :width :max-width
                                :height :max-height) (str (:value min) "px")})))

(comment
  (size-style :width nil)
  (size-style :width {:min {:type :px :value 10}})
  (size-style :width {:value {:type :part :value 2}})
  (size-style :width {:value {:type :px :value 100}}))


;; OVERFLOW

(def overflow-scroll "ofs")
(def overflow-scroll-y "ofsy")
(def overflow-scroll-x "ofsx")
(def overflow-clip "ofc")
(def overflow-clip-y "ofcy")
(def overflow-clip-x "ofcx")

(defn overflow-style [{:keys [x y]}]
  (if (and (nil? x) (nil? y))
    empty-style-def
    (let [classes (if (and (= x y) (not (nil? x)))
                    (case x
                      :scroll (list overflow-scroll)
                      :clip (list overflow-clip))
                    (cond-> (list)
                            (not (nil? x)) (conj (case x
                                                   :scroll overflow-scroll-x
                                                   :clip overflow-clip-x))
                            (not (nil? y)) (conj (case x
                                                   :scroll overflow-scroll-y
                                                   :clip overflow-clip-y))))
          ]
      {:classes     classes
       :style-sheet {}})))

(comment
  (overflow-style {:x :clip})
  (overflow-style {:y :clip :x :clip})
  (overflow-style {:y :clip :x :scroll}))

;; SPACING

(defn gap-style [{:keys [x y]}]
  (if (and (nil? x) (nil? y))
    empty-style-def
    (let [name (str "ga-" x)]
      {:classes     #{name}
       :style-sheet {(str (class name) "> *")             {:margin-right (str x "px")}
                     (str (class name) "> *:first-child") {:margin-right 0}}})))

(comment
  (gap-style {:x 20}))

(defn padding-style [{:keys [top right bottom left]}]
  (if (or (nil? top) (nil? right) (nil? bottom) (nil? left))
    empty-style-def
    (let [name (str "pa-" top "-" right "-" bottom "-" left)]
      {:classes     #{name}
       :style-sheet {(class name) {:padding (str top "px " right "px " bottom "px " left "px")}}})))

(comment
  (padding-style {:top 5 :right 5 :bottom 10 :left 5}))


;; STATIC STYLES

(def static-style-sheet {;; natural box model
                         "html"                                                        {:box-sizing  "border-box"
                                                                                        :font-family "sans-serif"}
                         "*, *:before, *:after"                                        {:box-sizing "inherit"}

                         ;; elements

                         (class box)                                                   {:min-width  "0"
                                                                                        :min-height "0"}

                         (class row)                                                   {:display        "inline-flex"
                                                                                        :flex-direction "row"}

                         (class column)                                                {:display        "inline-flex"
                                                                                        :flex-direction "column"}

                         (class button)                                                {:border    "0"
                                                                                        :padding   "0"
                                                                                        :font-size "inherit"}

                         ;; overflow

                         (class overflow-clip)                                         {:overflow "hidden"}
                         (class overflow-clip-x)                                       {:overflow-x "hidden"}
                         (class overflow-clip-y)                                       {:overflow-y "hidden"}
                         (class overflow-scroll)                                       {:overflow "scroll"}
                         (class overflow-scroll-x)                                     {:overflow-x "scroll"}
                         (class overflow-scroll-y)                                     {:overflow-y "scroll"}

                         ;; size

                         (str/join ","
                                   (list (class (for-dimension :width size-shrink))
                                         (class (for-dimension :height size-shrink)))) {:display "inline-block"}
                         (class (for-dimension :width size-fill))                      {:width "100%"}
                         (class (for-dimension :height size-fill))                     {:height "100%"}})

(def static-css (style-sheet->css static-style-sheet))

(defn child-style-sheets [element]
  (let [style-sheet (or (:style-sheet element) {})]
    (merge style-sheet
           (apply merge (map child-style-sheets (:children element))))))

(comment
  (child-style-sheets {:style-sheet {:a "2"} :children (list {:style-sheet {:b "3"}})}))


(defn get-css [element]
  (let [dynamic-css (-> element
                        (child-style-sheets)
                        (style-sheet->css))]
    (str static-css "\n\n" dynamic-css)))

(comment ())
