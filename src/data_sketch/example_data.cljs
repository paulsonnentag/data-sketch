(ns data-sketch.example-data)

(def todo [; projects
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
           {:ds/tag       "todo"
            :todo/text    "Displaying list of todos"
            :todo/tags    ["listen" "query"]
            :todo/project -2
            :todo/done    true
            :todo/due     "2014-12-13"}
           {:ds/tag       "todo"
            :todo/text    "Persisting to localStorage"
            :todo/tags    ["listen" "serialization" "transact"]
            :todo/project -2
            :todo/done    true
            :todo/due     "2014-12-13"}
           {:ds/tag       "todo"
            :todo/text    "Make task completable"
            :todo/tags    ["transact" "funs"]
            :todo/project -2
            :todo/done    false
            :todo/due     "2014-12-13"}
           {:ds/tag       "todo"
            :todo/text    "Fix fn calls on emtpy rels"
            :todo/tags    ["bug" "funs" "query"]
            :todo/project -1
            :todo/done    false
            :todo/due     "2015-01-01"}
           {:ds/tag       "todo"
            :todo/text    "Add db filtering"
            :todo/project -1
            :todo/done    false
            :todo/due     "2015-05-30"}
           {:ds/tag       "todo"
            :todo/text    "Soap"
            :todo/project -3
            :todo/done    false
            :todo/due     "2015-05-01"}
           {:ds/tag       "todo"
            :todo/text    "Cake"
            :todo/done    false
            :todo/project -3}
           {:ds/tag    "todo"
            :todo/text "Just a task" :todo/done false}
           {:ds/tag    "todo"
            :todo/text "Another incomplete task" :todo/done false}])


(def movies
  (let [[rainman-forever die-hard hairy-plumber rent-and-rentability little-schemer
         old-joes amc-lilliput ua-easy little-end gulliver-theater landmark-quinbus plt-arthouse] (iterate dec -1)]
    [;Movies
     {:db/id             rainman-forever
      :ds/tag            "movie"
      :movie/title       "Rainman Forever"
      :movie/category    "Action"
      :movie/description "An autistic man fights crime on the streest of Gotham."
      :movie/rating      4
      }
     {:db/id             die-hard
      :ds/tag            "movie"
      :movie/title       "Die Hard With More Intensity"
      :movie/category    "Drama"
      :movie/description "A streetwise cop confronts loneliness in Tokyo."
      :movie/rating      4}
     {:db/id             hairy-plumber
      :ds/tag            "movie"
      :movie/title       "Hairy Plumber and the Goomba of Doom"
      :movie/category    "Fantasy"
      :movie/description "Mario and Luigi attend a school of wizardry."
      :movie/rating      3.5}
     {:db/id             rent-and-rentability
      :ds/tag            "movie"
      :movie/title       "Rent and Rentability"
      :movie/category    "Romance"
      :movie/description "Dissimilar sisters seek husbands in the East Village."
      :movie/rating      3.5}
     {:db/id             little-schemer
      :ds/tag            "movie"
      :movie/title       "The Little Schemer"
      :movie/category    "Adventure"
      :movie/description "An elephant journeys to find Lambda the Ultimate"
      :movie/rating      3.5}

     ;Theaters
     {:db/id        old-joes
      :ds/tag       "theater"
      :theater/name "Old Joe's Showhouse"}
     {:db/id        amc-lilliput
      :ds/tag       "theater"
      :theater/name "AMC Lilliput"}
     {:db/id        ua-easy
      :ds/tag       "theater"
      :theater/name "UA Easy Street"}
     {:db/id        little-end
      :ds/tag       "theater"
      :theater/name "Little End Cinemas"}
     {:db/id        gulliver-theater
      :ds/tag       "theater"
      :theater/name "Gulliver Theater"}
     {:db/id        landmark-quinbus
      :ds/tag       "theater"
      :theater/name "Landmark Quinbus"}
     {:db/id        plt-arthouse
      :ds/tag       "theater"
      :theater/name "PLT Arthouse"}

     ;Showtimes
     {:ds/tag           "showtime"
      :showtime/time    "11:55"
      :showtime/theater old-joes
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "3:00"
      :showtime/theater old-joes
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "19:15"
      :showtime/theater old-joes
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "13:25"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "14:45"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "16:20"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "18:15"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "19:30"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "21:25"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "22:35"
      :showtime/theater amc-lilliput
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "12:45"
      :showtime/theater ua-easy
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "15:00"
      :showtime/theater ua-easy
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "17:00"
      :showtime/theater ua-easy
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "19:20"
      :showtime/theater ua-easy
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "21:00"
      :showtime/theater ua-easy
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "23:00"
      :showtime/theater ua-easy
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "13:40"
      :showtime/theater gulliver-theater
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "16:25"
      :showtime/theater gulliver-theater
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "17:20"
      :showtime/theater gulliver-theater
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "22:00"
      :showtime/theater gulliver-theater
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "14:15"
      :showtime/theater little-end
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "16:45"
      :showtime/theater little-end
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "19:15"
      :showtime/theater little-end
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "21:35"
      :showtime/theater little-end
      :showtime/movie   rainman-forever}
     {:ds/tag           "showtime"
      :showtime/time    "11:45"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "12:00"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "12:40"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "13:20"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "14:00"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "15:30"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "17:00"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "18:30"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "19:30"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "20:05"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "20:30"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "21:15"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "21:50"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "22:10"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "23:30"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "00:00"
      :showtime/theater amc-lilliput
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "13:00"
      :showtime/theater ua-easy
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "14:30"
      :showtime/theater ua-easy
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "16:30"
      :showtime/theater ua-easy
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "19:00"
      :showtime/theater ua-easy
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "20:50"
      :showtime/theater ua-easy
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "22:00"
      :showtime/theater ua-easy
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "16:30"
      :showtime/theater landmark-quinbus
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "20:00"
      :showtime/theater landmark-quinbus
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "22:00"
      :showtime/theater landmark-quinbus
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "23:50"
      :showtime/theater landmark-quinbus
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "11:55"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "13:15"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "14:30"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "15:30"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "17:05"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "18:25"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "19:40"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "21:05"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "22:15"
      :showtime/theater little-end
      :showtime/movie   die-hard}
     {:ds/tag           "showtime"
      :showtime/time    "12:00"
      :showtime/theater amc-lilliput
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "14:25"
      :showtime/theater amc-lilliput
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "16:45"
      :showtime/theater amc-lilliput
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "19:05"
      :showtime/theater amc-lilliput
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "21:25"
      :showtime/theater amc-lilliput
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "12:40"
      :showtime/theater ua-easy
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "15:55"
      :showtime/theater ua-easy
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "19:15"
      :showtime/theater ua-easy
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "22:20"
      :showtime/theater ua-easy
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "00:15"
      :showtime/theater ua-easy
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "12:05"
      :showtime/theater landmark-quinbus
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "14:45"
      :showtime/theater landmark-quinbus
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "20:30"
      :showtime/theater gulliver-theater
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "22:30"
      :showtime/theater gulliver-theater
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "11:30"
      :showtime/theater little-end
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "13:35"
      :showtime/theater little-end
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "15:40"
      :showtime/theater little-end
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "17:45"
      :showtime/theater little-end
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "19:50"
      :showtime/theater little-end
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "22:00"
      :showtime/theater little-end
      :showtime/movie   hairy-plumber}
     {:ds/tag           "showtime"
      :showtime/time    "13:15"
      :showtime/theater old-joes
      :showtime/movie   rent-and-rentability}
     {:ds/tag           "showtime"
      :showtime/time    "17:15"
      :showtime/theater old-joes
      :showtime/movie   rent-and-rentability}
     {:ds/tag           "showtime"
      :showtime/time    "21:30"
      :showtime/theater old-joes
      :showtime/movie   rent-and-rentability}
     {:ds/tag           "showtime"
      :showtime/time    "12:35"
      :showtime/theater gulliver-theater
      :showtime/movie   rent-and-rentability}
     {:ds/tag           "showtime"
      :showtime/time    "14:20"
      :showtime/theater gulliver-theater
      :showtime/movie   rent-and-rentability}
     {:ds/tag           "showtime"
      :showtime/time    "16:55"
      :showtime/theater gulliver-theater
      :showtime/movie   rent-and-rentability}
     {:ds/tag           "showtime"
      :showtime/time    "20:20"
      :showtime/theater gulliver-theater
      :showtime/movie   rent-and-rentability}
     {:ds/tag           "showtime"
      :showtime/time    "23:10"
      :showtime/theater gulliver-theater
      :showtime/movie   rent-and-rentability}
     {:ds/tag           "showtime"
      :showtime/time    "19:00"
      :showtime/theater little-end
      :showtime/movie   rent-and-rentability}
     {:ds/tag           "showtime"
      :showtime/time    "21:30"
      :showtime/theater little-end
      :showtime/movie   rent-and-rentability}
     {:ds/tag           "showtime"
      :showtime/time    "23:30"
      :showtime/theater little-end
      :showtime/movie   rent-and-rentability}
     {:ds/tag           "showtime"
      :showtime/time    "19:30"
      :showtime/theater plt-arthouse
      :showtime/movie   little-schemer}
     {:ds/tag           "showtime"
      :showtime/time    "21:30"
      :showtime/theater plt-arthouse
      :showtime/movie   little-schemer}

     ;reviews
     {:ds/tag "review"
      :review/movie rainman-forever
      :review/quote "137 interminable minutes. I counted them."
      :review/author "Ebert"}
     {:ds/tag "review"
      :review/movie rainman-forever
      :review/quote "He's an excellent driver in a terrible movie."
      :review/author "Boston Sun"}
     {:ds/tag "review"
      :review/movie rainman-forever
      :review/quote "A flop. Definitely a flop..."
      :review/author "SF Chronicle"}
     {:ds/tag "review"
      :review/movie die-hard
      :review/quote "Extraordinary powerful ... A masterpiece of cinema."
      :review/author "Ebert"}
     {:ds/tag "review"
      :review/movie die-hard
      :review/quote "Beautiful and haunting."
      :review/author "filmscritic.com"}
     {:ds/tag "review"
      :review/movie die-hard
      :review/quote "Moves slow but packs a punch."
      :review/author "Boston Sun"}
     {:ds/tag "review"
      :review/movie rent-and-rentability
      :review/quote "A poor adaption of the Broadway hit."
      :review/author "Ebert"}
     {:ds/tag "review"
      :review/movie rent-and-rentability
      :review/quote "A real tear-jerker. Keep your hanky handy!"
      :review/author "filmscritic.com"}
     {:ds/tag "review"
      :review/movie little-schemer
      :review/quote "Cons is magnificent! ... Add this movie to your list!"
      :review/author "Ebert"}

     ;credit
     {:ds/tag "credit"
      :credit/name "Uwe Boll"
      :credit/movie rainman-forever}
     {:ds/tag "credit"
      :credit/name "Dustin Hoffman"
      :credit/movie rainman-forever}
     {:ds/tag "credit"
      :credit/name "Jim Carrey"
      :credit/movie rainman-forever}
     {:ds/tag "credit"
      :credit/name "Jet Li"
      :credit/movie rainman-forever}
     {:ds/tag "credit"
      :credit/name "Sofia Coppola"
      :credit/movie die-hard}
     {:ds/tag "credit"
      :credit/name "Bill Murray"
      :credit/movie die-hard}
     {:ds/tag "credit"
      :credit/name "Bruce Willis"
      :credit/movie die-hard}
     {:ds/tag "credit"
      :credit/name "Jet Li"
      :credit/movie die-hard}
     {:ds/tag "credit"
      :credit/name "Steven Spielberg"
      :credit/movie hairy-plumber}
     {:ds/tag "credit"
      :credit/name "Daniel Radcliffe"
      :credit/movie hairy-plumber}
     {:ds/tag "credit"
      :credit/name "Emma Watson"
      :credit/movie hairy-plumber}
     {:ds/tag "credit"
      :credit/name "Jet Li"
      :credit/movie hairy-plumber}
     {:ds/tag "credit"
      :credit/name "Ang Lee"
      :credit/movie rent-and-rentability}
     {:ds/tag "credit"
      :credit/name "Emma Thompson"
      :credit/movie rent-and-rentability}
     {:ds/tag "credit"
      :credit/name "Kate Winslet"
      :credit/movie rent-and-rentability}
     {:ds/tag "credit"
      :credit/name "Jet Li"
      :credit/movie rent-and-rentability}
     {:ds/tag "credit"
      :credit/name "Friedman & Felleisen"
      :credit/movie little-schemer}
     {:ds/tag "credit"
      :credit/name "Car"
      :credit/movie little-schemer}
     {:ds/tag "credit"
      :credit/name "Cdr"
      :credit/movie little-schemer}
     {:ds/tag "credit"
      :credit/name "Cons"
      :credit/movie little-schemer}
     {:ds/tag "credit"
      :credit/name "Cond"
      :credit/movie little-schemer}
     ]))
