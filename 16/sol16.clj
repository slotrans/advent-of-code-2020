(ns net.blergh.advent2020
    (:require [clojure.string :as str]
              [clojure.edn :as edn]
              [clojure.set :as set]
    )
)

; INPUT EXCERPT
;
; seat: 46-356 or 369-973
; train: 43-414 or 423-954
; type: 35-160 or 178-950
; wagon: 29-878 or 889-959
; zone: 31-188 or 201-971
; 
; your ticket:
; 137,149,139,127,83,61,89,53,73,67,131,113,109,101,71,59,103,97,107,79
; 
; nearby tickets:
; 390,125,294,296,621,356,716,135,845,790,433,348,710,927,863,136,834,139,115,323
; 819,227,432,784,840,691,760,608,352,759,85,712,578,575,901,151,440,494,283,274
; 455,784,136,934,493,390,140,53,397,355,802,100,420,126,902,870,588,498,60,607
; 84,785,235,760,316,787,318,70,809,586,228,388,458,152,408,245,983,765,485,348
; 71,303,390,394,68,796,372,829,153,656,769,103,827,588,873,595,619,149,235,785
; 494,323,586,945,847,75,839,606,586,457,355,840,114,376,753,207,205,823,273,840


; produces { "class" ((1 3) (5 7))
;          , "row" ((6 11) (33 44))
;          , "seat" ((13 40) (45 50))
;          }
; in theory each value is a sequence of ranges (each defined by inclusive bounds)
; but in practice there are always exactly two such ranges
(defn extract-field-ranges [input]
    (let [ input-lines (str/split input #"\n")
         ]
        (into 
            {}
            (for [line input-lines]
                (let [ [field-name range-part] (str/split line #": ")
                     , range-strings (str/split range-part #" or ")
                     , ranges (for [x range-strings] 
                                  (for [y (str/split x #"-")] 
                                      (edn/read-string y)))
                     ]
                    {field-name ranges}
                )
            )
        )
    )
)

; produces (7 1 14)
(defn extract-your-ticket [input]
    (map 
        edn/read-string 
        (str/split 
            (second (str/split input #"\n")) 
            #","))
)

; produces ((7 3 47) 
;           (40 4 50) 
;           (55 2 20) 
;           (38 6 12))
(defn extract-nearby-tickets [input]
    (let [ temp-input-lines (str/split input #"\n")
         , input-lines (rest temp-input-lines) ; throw away the "nearby tickets:" line
         ]
        (for [line input-lines]
            (for [num (str/split line #",")]
                (edn/read-string num)
            )
        )
    )
)


(defn is-num-valid-anywhere? [num field-ranges]
    (some
        true?
        (flatten
            (for [range-list (vals field-ranges)]
                (for [range range-list]
                    (<= (first range) num (second range))
                )
            )
        )
    )
)

(defn find-invalid-values [tickets field-ranges]
    (filter
        some?
        (flatten
            (for [ticket tickets]
                (for [num ticket]
                    (if (is-num-valid-anywhere? num field-ranges)
                        nil
                        num
                    )
                )
            )
        )
    )
)


(defn structure-input [input]
    (let [ chunks (str/split input #"\n\n")
         ]
        { :field-ranges (extract-field-ranges (nth chunks 0))
        , :your-ticket (extract-your-ticket (nth chunks 1))
        , :nearby-tickets (extract-nearby-tickets (nth chunks 2))
        }
    )
)


(def sample-input 
"class: 1-3 or 5-7
row: 6-11 or 33-44
seat: 13-40 or 45-50

your ticket:
7,1,14

nearby tickets:
7,3,47
40,4,50
55,2,20
38,6,12"
)
(def structured-sample-input (structure-input sample-input))
(def sample-invalid-values (find-invalid-values (:nearby-tickets structured-sample-input) (:field-ranges structured-sample-input)))
(println "(sample) invalid values" sample-invalid-values)
(def sample-invalid-values-sum (apply + sample-invalid-values))
(println "(sample) ticket scanning error rate" sample-invalid-values-sum)

(def input16 (slurp "input16.txt"))
(def structured-input16 (structure-input input16))
(def invalid-values (find-invalid-values (:nearby-tickets structured-input16) (:field-ranges structured-input16)))
(println "(p1) invalid values" invalid-values)
(def invalid-values-sum (apply + invalid-values))
(println "(p1) ticket scanning error rate" invalid-values-sum)


; part 2

(defn discard-invalid-tickets [tickets field-ranges]
    (filter
        some?
        (for [ticket tickets]
            (if (> (count (find-invalid-values [ticket] field-ranges)) 0)
                nil
                ticket
            )
        )
    )
)


(defn find-valid-fields [num field-ranges]
    (into
        #{}
        (filter
            some?
            (flatten
                (for [field-entry field-ranges]
                    (for [range (val field-entry)]
                        (if (<= (first range) num (second range))
                            (key field-entry)
                            nil
                        )
                    )
                )
            )
        )
    )
)


; a ticket is a vector of fields
; a field is a map...
; { :num 3
; , :possible-fields #{"row" "seat"}
; }

(defn enrich-ticket [ticket field-ranges]
    (vec
        (for [num ticket]
            { :num num
            , :possible-fields (find-valid-fields num field-ranges)
            }
        )
    )
)

(defn enrich-all-tickets [tickets field-ranges]
    (mapv
        #(enrich-ticket % field-ranges)
        tickets
    )
)

(def p2-sample-input
"class: 0-1 or 4-19
row: 0-5 or 8-19
seat: 0-13 or 16-19

your ticket:
11,12,13

nearby tickets:
3,9,18
15,1,5
5,14,9")
(def structured-p2-sample-input (structure-input p2-sample-input))
(def validated-structured-p2-sample-input
    (assoc 
        structured-p2-sample-input 
        :nearby-tickets 
        (discard-invalid-tickets (:nearby-tickets structured-p2-sample-input) (:field-ranges structured-p2-sample-input))
    )
)
(def enriched-tickets-p2-sample 
    (enrich-all-tickets (:nearby-tickets validated-structured-p2-sample-input) (:field-ranges validated-structured-p2-sample-input))
)
(doseq [t enriched-tickets-p2-sample]
    (println t))
