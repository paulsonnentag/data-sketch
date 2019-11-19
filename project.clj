(defproject data-sketch "1.1.7"
  :description "rethinking how to build graphical software interfaces"
  :source-paths ["src"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.521"]
                 [rum "0.11.4"]]
  :plugins [[lein-cljsbuild "1.1.7"]]
  :clean-targets [:target-path]
  :cljsbuild {:builds
              {:dev
               {:source-paths ["src"]
                :compiler     {:output-to     "resources/public/js/main.js"
                               :optimizations :whitespace
                               :pretty-print  true}}}})
