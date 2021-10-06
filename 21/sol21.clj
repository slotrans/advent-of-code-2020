(ns net.blergh.advent2020
    (:require [clojure.string :as str]
              [clojure.set :as set]
    )
)


(defn divide-input-line [s]
    (-> s
        (str/replace ,,, "(" "")
        (str/replace ,,, ")" "")
        (str/split ,,, #" contains ")
    )    
)


(defn parse-input [s]
    (vec
        (for [line (str/split-lines s)]
            (let [parts (divide-input-line line)]
                { :ingredients (set (str/split (first parts) #" "))
                , :allergens (set (str/split (second parts) #", "))
                }
            )
        )
    )
)



(def sample-input
"mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
trh fvjkl sbzzf mxmxvkd (contains dairy)
sqjhc fvjkl (contains soy)
sqjhc mxmxvkd sbzzf (contains fish)"
)

(def parsed-sample (parse-input sample-input))
(def sample-ingredients (apply set/union (map :ingredients parsed-sample)))
(def sample-allergens (apply set/union (map :allergens parsed-sample)))

(println sample-ingredients)
(println sample-allergens)
