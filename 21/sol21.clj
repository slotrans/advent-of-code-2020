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


(defn parse-input [s] ; -> vector of maps, each containing the ingredients and possible allergens from an input record
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


;; Since there are multiple records for each allergen, the ingredient carrying that allergen
;; must be in *all* of them. Intersecting the sets of ingredients gives us our possibilities.
(defn possible-ingredients-for-allergen [records allergen] ; -> set of ingredients
    (apply 
        set/intersection 
        (map 
            :ingredients 
            (filter 
                #(contains? (:allergens %) allergen)
                records
            )
        )
    )
)


;; Put all allergen possibilities into a map keyed on the allergens.
(defn seed-allergen-possibilities [records] ; -> map of allergens to sets of ingredients
    (into
        {}
        (let [all-allergens (apply set/union (map :allergens records))]
            (for [allergen all-allergens]
                {allergen (possible-ingredients-for-allergen records allergen)}
            )
        )
    )
)


;; If our possibilities map shows only one ingredient for an allergen, then it's solved.
;; We can then cross that ingredient off as a possibility for the other allergens and look again.
;;
;; This relies on the input having the property that after the first pass of possibility checking,
;; there is at least one unambiguously-identified allergen:ingredient pair, and that iteratively
;; deducing from there leads to a full set of conclusions.
(defn solve-allergens [possibilities] ; -> map of allergens to their guilty ingredients
    (println (str "possibilities: " possibilities))
    (if (every? #(= 1 (count (val %))) possibilities)
        possibilities
        (let [ updated-possibilities
                (reduce
                    (fn [state element] ; `state`=possiblities map, `element`=an allergen
                        (let [ingredients (get state element)]
                            (if (= 1 (count ingredients))
                                ;; there's only one possible ingredient for this allergen
                                (into
                                    {}
                                    (for [[k v] state]
                                        (if (= k element)
                                            {k v} ; leave the ingredient associated with this allergen
                                            {k (disj v (first ingredients))} ; remove the ingredient from all other allergens' sets
                                        )
                                    )
                                )
                                ;; tells us nothing, return state unchanged
                                state
                            )
                        )
                    )
                    possibilities
                    (keys possibilities)
                )
             ]
            (recur updated-possibilities)
        )
    )
)


(defn compute-p1-answer [records solution]
    (let [allergen-carrying-ingredients (apply set/union (vals solution))]
        (apply 
            +
            (for [rec records]
                (count (remove allergen-carrying-ingredients (:ingredients rec)))
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

(println "SAMPLE")
(println (str "sample ingredients: " sample-ingredients))
(println (str "sample allergens: " sample-allergens))

(def sample-allergen-ingredient-map (solve-allergens (seed-allergen-possibilities parsed-sample)))
(println (str "sample solved allergen/ingredient pairs: " sample-allergen-ingredient-map))

(def sample-answer (compute-p1-answer parsed-sample sample-allergen-ingredient-map))
(println (str "sample answer: " sample-answer)) ; 5


(def input21 (slurp "input21.txt"))
(def parsed-input21 (parse-input input21))
(def input21-ingredients (apply set/union (map :ingredients parsed-input21)))
(def input21-allergens (apply set/union (map :allergens parsed-input21)))

(println "Part 1")
(println (str "p1 ingredients: " input21-ingredients))
(println (str "p1 allergens: " input21-allergens))

(def input21-allergen-ingredient-map (solve-allergens (seed-allergen-possibilities parsed-input21)))
(println (str "p1 solved allergen/ingredient pairs: " input21-allergen-ingredient-map))

(def input21-answer (compute-p1-answer parsed-input21 input21-allergen-ingredient-map))
(println (str "p1 answer: " input21-answer)) ; 2734
