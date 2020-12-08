;F=0, B=1
;L=0, R=1
;
;So, decoding FBFBBFFRLR reveals that it is the seat at row 44, column 5.
;
;Every seat also has a unique seat ID: multiply the row by 8, then add the column. 
;In this example, the seat has ID 44 * 8 + 5 = 357.
;
;Here are some other boarding passes:
;
;    BFFFBBFRRR: row 70, column 7, seat ID 567.
;    FFFBBBFRRR: row 14, column 7, seat ID 119.
;    BBFFBBFRLL: row 102, column 4, seat ID 820.
;
;As a sanity check, look through your list of boarding passes. What is the highest seat ID on a boarding pass?

; PART 2
;It's a completely full flight, so your seat should be the only missing boarding pass in your list. 
;However, there's a catch: some of the seats at the very front and back of the plane don't exist on this aircraft, 
;so they'll be missing from your list as well.

;Your seat wasn't at the very front or back, though; the seats with IDs +1 and -1 from yours will be in your list.

(ns net.blergh.advent2020
    (:require [clojure.string :as str]
              [clojure.edn :as edn]
              [clojure.set :as set]
    )
)

(def input05 (slurp "input05"))

(def input-lines (str/split input05 #"\n"))

(defn decode-boarding-pass [x]
    (let [ row-part (subs x 0 7)
         , col-part (subs x 7 10)
         , row-binary-str (str/replace (str/replace row-part "F" "0") "B" "1")
         , col-binary-str (str/replace (str/replace col-part "L" "0") "R" "1")
         , row-num (edn/read-string (str "2r" row-binary-str))
         , col-num (edn/read-string (str "2r" col-binary-str))
         , seat-id (+ (* row-num 8) col-num)
         ]
         { :code x
         , :row row-num
         , :col col-num
         , :seat-id seat-id
         }
    )
)

;(println (decode-boarding-pass "FBFBBFFRLR"))

(def decoded-passes
    (for [line input-lines]
        (decode-boarding-pass line)
    )
)

(def taken-seat-ids
    (set (map :seat-id decoded-passes)) ; important that this be a SET, contains? does not work as desired with vectors
)

(println "part 1")
(println
    (apply max taken-seat-ids)
)
; part 1answer: 850


(defn find-empty-seat [seats-to-check taken-seats]
    (if (empty? seats-to-check)
        nil
        (let [seat (first seats-to-check)]
            ;(println (str seat ": "
            ;            "taken=" (contains? taken-seats seat) 
            ;            ", -1 taken=" (contains? taken-seats (- seat 1)) 
            ;            ", +1 taken=" (contains? taken-seats (+ seat 1)))
            ;)
            (if (and
                    (not (contains? taken-seats seat))
                    (contains? taken-seats (- seat 1))
                    (contains? taken-seats (+ seat 1))
                )
                seat
                (find-empty-seat (rest seats-to-check) taken-seats)
            )
        )
    )
)

(println "part 2")
; brute force: just try every possible seat ID, there aren't very many
(println (str "the empty seat ID is: " (find-empty-seat (range 0 1024) taken-seat-ids)))
