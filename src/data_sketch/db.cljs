(ns data-sketch.db
  (:require [datascript.core :as d]))

(def base-facts [{:db/ident :ds.kind/name :db/valueType :db.type/string}
                 {:db/ident :ds.kind/attribute :db/valueType :db.type/ref :db/cardinality :db.cardinality/many}

                 {:db/ident :ds/refKind :db/valueType :db.type/ref :db/cardinality :db.cardinality/many}

                 {:db/ident :ds.query/kind :db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
                 {:db/ident :ds.query/column :db/valueType :db.type/ref :db/cardinality :db.cardinality/many}])

(def movie-dataset
  (let [[movie, movie-title, movie-category, movie-description, movie-rating,
         theater, theater-name,
         showtime, showtime-time, showtime-theater, showtime-movie
         review, review-movie, review-author, review-quote,
         credit, credit-movie, credit-name,
         rainman-forever die-hard hairy-plumber rent-and-rentability little-schemer
         old-joes amc-lilliput ua-easy little-end gulliver-theater landmark-quinbus plt-arthouse] (iterate dec -1)]

    [
     ;---  MOVIE

     {:db/id        movie
      :ds.kind/name "Movie"
      :ds.kind/attribute [movie-title, movie-category, movie-description, movie-rating]}

     {:db/id          movie-title
      :db/ident       :movie/title
      :db/valueType   :db.type/string
      :db/cardinality :db.cardinality/one}
     {:db/id          movie-category
      :db/ident       :movie/category
      :db/valueType   :db.type/string
      :db/cardinality :db.cardinality/many}
     {:db/id          movie-description
      :db/ident       :movie/description
      :db/valueType   :db.type/string
      :db/cardinality :db.cardinality/one}
     {:db/id          movie-rating
      :db/ident       :movie/rating
      :db/valueType   :db.type/string
      :db/cardinality :db.cardinality/one}

     {:db/id             rainman-forever
      :movie/title       "Rainman Forever"
      :movie/category    "Action"
      :movie/description "An autistic man fights crime on the streest of Gotham."
      :movie/rating      4}
     {:db/id             die-hard
      :movie/title       "Die Hard With More Intensity"
      :movie/category    "Drama"
      :movie/description "A streetwise cop confronts loneliness in Tokyo."
      :movie/rating      4}
     {:db/id             hairy-plumber
      :movie/title       "Hairy Plumber and the Goomba of Doom"
      :movie/category    "Fantasy"
      :movie/description "Mario and Luigi attend a school of wizardry."
      :movie/rating      3.5}
     {:db/id             rent-and-rentability
      :movie/title       "Rent and Rentability"
      :movie/category    "Romance"
      :movie/description "Dissimilar sisters seek husbands in the East Village."
      :movie/rating      3.5}
     {:db/id             little-schemer
      :movie/title       "The Little Schemer"
      :movie/category    "Adventure"
      :movie/description "An elephant journeys to find Lambda the Ultimate"
      :movie/rating      3.5}

     ;-- THEATER

     {:db/id        theater
      :ds.kind/name "Theater"
      :ds.kind/attribute [theater-name]
      }

     {:db/id          theater-name
      :db/ident       :theater/name
      :db/valueType   :db.type/string
      :db/cardinality :db.cardinality/one}

     {:db/id        old-joes
      :theater/name "Old Joe's Showhouse"}
     {:db/id        amc-lilliput
      :theater/name "AMC Lilliput"}
     {:db/id        ua-easy
      :theater/name "UA Easy Street"}
     {:db/id        little-end
      :theater/name "Little End Cinemas"}
     {:db/id        gulliver-theater
      :theater/name "Gulliver Theater"}
     {:db/id        landmark-quinbus
      :theater/name "Landmark Quinbus"}
     {:db/id        plt-arthouse
      :theater/name "PLT Arthouse"}

     ;--- SHOWTIME

     {:db/id        showtime
      :ds.kind/name "Showtime"
      :ds.kind/attribute [showtime-time]
      }

     {:db/id          showtime-time
      :db/ident       :showtime/time
      :db/valueType   :db.type/string
      :db/cardinality :db.cardinality/one}

     {:db/id          showtime-movie
      :db/ident       :showtime/movie
      :db/valueType   :db.type/ref
      :db/cardinality :db.cardinality/one
      :ds/refKind     movie}

     {:db/id          showtime-theater
      :db/ident       :showtime/theater
      :db/valueType   :db.type/ref
      :db/cardinality :db.cardinality/one
      :ds/refKind     theater}

     {:showtime/time    "11:55"
      :showtime/theater old-joes
      :showtime/movie   rainman-forever}
     {:showtime/time    "3:00"
      :showtime/theater old-joes
      :showtime/movie   rainman-forever}
     {:showtime/time    "19:15"
      :showtime/theater old-joes
      :showtime/movie   rainman-forever}
     {:showtime/time    "13:25"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:showtime/time    "14:45"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:showtime/time    "16:20"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:showtime/time    "18:15"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:showtime/time    "19:30"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:showtime/time    "21:25"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:showtime/time    "22:35"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:showtime/time    "12:45"
      :showtime/theater ua-easy
      :showtime/movie   rainman-forever}
     {:showtime/time    "15:00"
      :showtime/theater ua-easy
      :showtime/movie   rainman-forever}
     {:showtime/time    "17:00"
      :showtime/theater ua-easy
      :showtime/movie   rainman-forever}
     {:showtime/time    "19:20"
      :showtime/theater ua-easy
      :showtime/movie   rainman-forever}
     {:showtime/time    "21:00"
      :showtime/theater ua-easy
      :showtime/movie   rainman-forever}
     {:showtime/time    "23:00"
      :showtime/theater ua-easy
      :showtime/movie   rainman-forever}
     {:showtime/time    "13:40"
      :showtime/theater gulliver-theater
      :showtime/movie   rainman-forever}
     {:showtime/time    "16:25"
      :showtime/theater gulliver-theater
      :showtime/movie   rainman-forever}
     {:showtime/time    "17:20"
      :showtime/theater gulliver-theater
      :showtime/movie   rainman-forever}
     {:showtime/time    "22:00"
      :showtime/theater gulliver-theater
      :showtime/movie   rainman-forever}
     {:showtime/time    "14:15"
      :showtime/theater little-end
      :showtime/movie   rainman-forever}
     {:showtime/time    "16:45"
      :showtime/theater little-end
      :showtime/movie   rainman-forever}
     {:showtime/time    "19:15"
      :showtime/theater little-end
      :showtime/movie   rainman-forever}
     {:showtime/time    "21:35"
      :showtime/theater little-end
      :showtime/movie   rainman-forever}
     {:showtime/time    "11:45"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "12:00"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "12:40"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "13:20"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "14:00"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "15:30"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "17:00"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "18:30"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "19:30"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "20:05"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "20:30"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "21:15"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "21:50"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "22:10"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "23:30"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "00:00"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:showtime/time    "13:00"
      :showtime/theater ua-easy
      :showtime/movie   die-hard}
     {:showtime/time    "14:30"
      :showtime/theater ua-easy
      :showtime/movie   die-hard}
     {:showtime/time    "16:30"
      :showtime/theater ua-easy
      :showtime/movie   die-hard}
     {:showtime/time    "19:00"
      :showtime/theater ua-easy
      :showtime/movie   die-hard}
     {:showtime/time    "20:50"
      :showtime/theater ua-easy
      :showtime/movie   die-hard}
     {:showtime/time    "22:00"
      :showtime/theater ua-easy
      :showtime/movie   die-hard}
     {:showtime/time    "16:30"
      :showtime/theater landmark-quinbus
      :showtime/movie   die-hard}
     {:showtime/time    "20:00"
      :showtime/theater landmark-quinbus
      :showtime/movie   die-hard}
     {:showtime/time    "22:00"
      :showtime/theater landmark-quinbus
      :showtime/movie   die-hard}
     {:showtime/time    "23:50"
      :showtime/theater landmark-quinbus
      :showtime/movie   die-hard}
     {:showtime/time    "11:55"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:showtime/time    "13:15"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:showtime/time    "14:30"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:showtime/time    "15:30"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:showtime/time    "17:05"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:showtime/time    "18:25"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:showtime/time    "19:40"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:showtime/time    "21:05"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:showtime/time    "22:15"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:showtime/time    "12:00"
      :showtime/theater amc-lilliput
      :showtime/movie   hairy-plumber}
     {:showtime/time    "14:25"
      :showtime/theater amc-lilliput
      :showtime/movie   hairy-plumber}
     {:showtime/time    "16:45"
      :showtime/theater amc-lilliput
      :showtime/movie   hairy-plumber}
     {:showtime/time    "19:05"
      :showtime/theater amc-lilliput
      :showtime/movie   hairy-plumber}
     {:showtime/time    "21:25"
      :showtime/theater amc-lilliput
      :showtime/movie   hairy-plumber}
     {:showtime/time    "12:40"
      :showtime/theater ua-easy
      :showtime/movie   hairy-plumber}
     {:showtime/time    "15:55"
      :showtime/theater ua-easy
      :showtime/movie   hairy-plumber}
     {:showtime/time    "19:15"
      :showtime/theater ua-easy
      :showtime/movie   hairy-plumber}
     {:showtime/time    "22:20"
      :showtime/theater ua-easy
      :showtime/movie   hairy-plumber}
     {:showtime/time    "00:15"
      :showtime/theater ua-easy
      :showtime/movie   hairy-plumber}
     {:showtime/time    "12:05"
      :showtime/theater landmark-quinbus
      :showtime/movie   hairy-plumber}
     {:showtime/time    "14:45"
      :showtime/theater landmark-quinbus
      :showtime/movie   hairy-plumber}
     {:showtime/time    "20:30"
      :showtime/theater gulliver-theater
      :showtime/movie   hairy-plumber}
     {:showtime/time    "22:30"
      :showtime/theater gulliver-theater
      :showtime/movie   hairy-plumber}
     {:showtime/time    "11:30"
      :showtime/theater little-end
      :showtime/movie   hairy-plumber}
     {:showtime/time    "13:35"
      :showtime/theater little-end
      :showtime/movie   hairy-plumber}
     {:showtime/time    "15:40"
      :showtime/theater little-end
      :showtime/movie   hairy-plumber}
     {:showtime/time    "17:45"
      :showtime/theater little-end
      :showtime/movie   hairy-plumber}
     {:showtime/time    "19:50"
      :showtime/theater little-end
      :showtime/movie   hairy-plumber}
     {:showtime/time    "22:00"
      :showtime/theater little-end
      :showtime/movie   hairy-plumber}
     {:showtime/time    "13:15"
      :showtime/theater old-joes
      :showtime/movie   rent-and-rentability}
     {:showtime/time    "17:15"
      :showtime/theater old-joes
      :showtime/movie   rent-and-rentability}
     {:showtime/time    "21:30"
      :showtime/theater old-joes
      :showtime/movie   rent-and-rentability}
     {:showtime/time    "12:35"
      :showtime/theater gulliver-theater
      :showtime/movie   rent-and-rentability}
     {:showtime/time    "14:20"
      :showtime/theater gulliver-theater
      :showtime/movie   rent-and-rentability}
     {:showtime/time    "16:55"
      :showtime/theater gulliver-theater
      :showtime/movie   rent-and-rentability}
     {:showtime/time    "20:20"
      :showtime/theater gulliver-theater
      :showtime/movie   rent-and-rentability}
     {:showtime/time    "23:10"
      :showtime/theater gulliver-theater
      :showtime/movie   rent-and-rentability}
     {:showtime/time    "19:00"
      :showtime/theater little-end
      :showtime/movie   rent-and-rentability}
     {:showtime/time    "21:30"
      :showtime/theater little-end
      :showtime/movie   rent-and-rentability}
     {:showtime/time    "23:30"
      :showtime/theater little-end
      :showtime/movie   rent-and-rentability}
     {:showtime/time    "19:30"
      :showtime/theater plt-arthouse
      :showtime/movie   little-schemer}
     {:showtime/time    "21:30"
      :showtime/theater plt-arthouse
      :showtime/movie   little-schemer}

     ;--- REVIEW
     {:db/id        review
      :ds.kind/name "Review"
      :ds.kind/attribute [review-author, review-movie, review-quote]}

     {:db/id          review-author,
      :db/ident       :review/author
      :db/valueType   :db.type/string
      :db/cardinality :db.cardinality/one}
     {:db/id          review-movie,
      :db/ident       :review/movie
      :db/valueType   :db.type/ref
      :db/cardinality :db.cardinality/one
      :ds/refKind     movie}
     {:db/id          review-quote,
      :db/ident       :review/quote
      :db/valueType   :db.type/string
      :db/cardinality :db.cardinality/one}

     {:review/movie  rainman-forever
      :review/quote  "137 interminable minutes. I counted them."
      :review/author "Ebert"}
     {:review/movie  rainman-forever
      :review/quote  "He's an excellent driver in a terrible movie."
      :review/author "Boston Sun"}
     {:review/movie  rainman-forever
      :review/quote  "A flop. Definitely a flop..."
      :review/author "SF Chronicle"}
     {:review/movie  die-hard
      :review/quote  "Extraordinary powerful ... A masterpiece of cinema."
      :review/author "Ebert"}
     {:review/movie  die-hard
      :review/quote  "Beautiful and haunting."
      :review/author "filmscritic.com"}
     {:review/movie  die-hard
      :review/quote  "Moves slow but packs a punch."
      :review/author "Boston Sun"}
     {:review/movie  rent-and-rentability
      :review/quote  "A poor adaption of the Broadway hit."
      :review/author "Ebert"}
     {:review/movie  rent-and-rentability
      :review/quote  "A real tear-jerker. Keep your hanky handy!"
      :review/author "filmscritic.com"}
     {:review/movie  little-schemer
      :review/quote  "Cons is magnificent! ... Add this movie to your list!"
      :review/author "Ebert"}

     ;--- CREDIT

     {:db/id        credit
      :ds.kind/name "Credit"
      :ds.kind/attribute [credit-movie, credit-name]
      }

     {:db/id          credit-movie,
      :db/ident       :credit/movie
      :db/valueType   :db.type/ref
      :db/cardinality :db.cardinality/one
      :ds/refKind     movie}
     {:db/id          credit-name,
      :db/ident       :credit/name
      :db/valueType   :db.type/string
      :db/cardinality :db.cardinality/one}

     {:credit/name  "Uwe Boll"
      :credit/movie rainman-forever}
     {:credit/name  "Dustin Hoffman"
      :credit/movie rainman-forever}
     {:credit/name  "Jim Carrey"
      :credit/movie rainman-forever}
     {:credit/name  "Jet Li"
      :credit/movie rainman-forever}
     {:credit/name  "Sofia Coppola"
      :credit/movie die-hard}
     {:credit/name  "Bill Murray"
      :credit/movie die-hard}
     {:credit/name  "Bruce Willis"
      :credit/movie die-hard}
     {:credit/name  "Jet Li"
      :credit/movie die-hard}
     {:credit/name  "Steven Spielberg"
      :credit/movie hairy-plumber}
     {:credit/name  "Daniel Radcliffe"
      :credit/movie hairy-plumber}
     {:credit/name  "Emma Watson"
      :credit/movie hairy-plumber}
     {:credit/name  "Jet Li"
      :credit/movie hairy-plumber}
     {:credit/name  "Ang Lee"
      :credit/movie rent-and-rentability}
     {:credit/name  "Emma Thompson"
      :credit/movie rent-and-rentability}
     {:credit/name  "Kate Winslet"
      :credit/movie rent-and-rentability}
     {:credit/name  "Jet Li"
      :credit/movie rent-and-rentability}
     {:credit/name  "Friedman & Felleisen"
      :credit/movie little-schemer}
     {:credit/name  "Car"
      :credit/movie little-schemer}
     {:credit/name  "Cdr"
      :credit/movie little-schemer}
     {:credit/name  "Cons"
      :credit/movie little-schemer}
     {:credit/name  "Cond"
      :credit/movie little-schemer}]
    ))

(def todo-dataset [; projects
                   {:db/id        -1
                    :ds/tag       "project"
                    :project/name "datascript"}
                   {:db/id        -2
                    :ds/tag       "project"
                    :project/name "nyc-webinar"}
                   {:db/id        -3
                    :ds/tag       "project"
                    :project/name "shopping"}

                   ; todos
                   {:todo/text    "Displaying list of todos"
                    :todo/tags    ["listen" "query"]
                    :todo/project -2
                    :todo/done    true
                    :todo/due     "2014-12-13"}
                   {:todo/text    "Persisting to localStorage"
                    :todo/tags    ["listen" "serialization" "transact"]
                    :todo/project -2
                    :todo/done    true
                    :todo/due     "2014-12-13"}
                   {:todo/text    "Make task completable"
                    :todo/tags    ["transact" "funs"]
                    :todo/project -2
                    :todo/done    false
                    :todo/due     "2014-12-13"}
                   {:todo/text    "Fix fn calls on emtpy rels"
                    :todo/tags    ["bug" "funs" "query"]
                    :todo/project -1
                    :todo/done    false
                    :todo/due     "2015-01-01"}
                   {:todo/text    "Add db filtering"
                    :todo/project -1
                    :todo/done    false
                    :todo/due     "2015-05-30"}
                   {:todo/text    "Soap"
                    :todo/project -3
                    :todo/done    false
                    :todo/due     "2015-05-01"}
                   {:todo/text    "Cake"
                    :todo/done    false
                    :todo/project -3}
                   {:todo/text "Just a task"
                    :todo/done false}
                   {:ds/tag    "todo"
                    :todo/text "Another incomplete task"
                    :todo/done false}])


(def initial-facts (concat base-facts movie-dataset))

(defn get-schema [facts]
  (->> facts
       (filter #(not (nil? (:db/ident %))))
       (reduce
         (fn [schema fact]
           (let [attribute-id (:db/ident fact)
                 attribute-fact (cond-> (dissoc fact :db/ident :db/id)
                                        (not= (:db/valueType fact) :db.type/ref) (dissoc :db/valueType))]


             (assoc schema attribute-id attribute-fact)))
         {})))


(defonce conn (d/create-conn (get-schema initial-facts)))

(defonce initial-data (d/transact! conn initial-facts))
