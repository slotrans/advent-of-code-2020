; eyr:2024 pid:662406624 hcl:#cfa07d byr:1947 iyr:2015 ecl:amb hgt:150cm
; 
; iyr:2013 byr:1997 hgt:182cm hcl:#ceb3a1
; eyr:2027
; ecl:gry cid:102 pid:018128535
; 
; hgt:61in iyr:2014 pid:916315544 hcl:#733820 ecl:oth
; 
; hcl:#a97842
; eyr:2026 byr:1980 ecl:grn pid:726519569 hgt:184cm cid:132 iyr:2011

;The expected fields are as follows:
;    byr (Birth Year)
;    iyr (Issue Year)
;    eyr (Expiration Year)
;    hgt (Height)
;    hcl (Hair Color)
;    ecl (Eye Color)
;    pid (Passport ID)
;    cid (Country ID)   <-- optional


(ns net.blergh.advent2020
    (:require [clojure.string :as str])
)

(defn get-passport-record [line]
    (loop [ kvpairs (str/split line #" ") 
          , passport {}
          ]
        (if (empty? kvpairs)
            passport
            (let [ fragment (first kvpairs)
                 , [k v] (str/split fragment #":")
                 ]
                (recur
                    (rest kvpairs)
                    (assoc passport (keyword k) v)
                )
            )
        )
    )
)

(defn is-passport-valid-part1? [passport]
    (and 
        (contains? passport :byr)
        (contains? passport :iyr)
        (contains? passport :eyr)
        (contains? passport :hgt)
        (contains? passport :hcl)
        (contains? passport :ecl)
        (contains? passport :pid)
    )
)

;four digits; at least 1920 and at most 2002
(defn is-byr-valid? [x] 
    (and 
        (re-matches #"[0-9]{4}" x)
        (<= 1920 (Integer/parseInt x) 2002)
    )
)

;four digits; at least 2010 and at most 2020
(defn is-iyr-valid? [x] 
    (and 
        (re-matches #"[0-9]{4}" x)
        (<= 2010 (Integer/parseInt x) 2020)
    )
)

;four digits; at least 2020 and at most 2030
(defn is-eyr-valid? [x]
    (and 
        (re-matches #"[0-9]{4}" x)
        (<= 2020 (Integer/parseInt x) 2030)
    )
)

;a number followed by either cm or in:
;    If cm, the number must be at least 150 and at most 193.
;    If in, the number must be at least 59 and at most 76.
(defn is-hgt-valid? [x] 
    (let [ [whole-match value unit] (re-matches #"([0-9]+)(cm|in)" x)
         ]
        (cond 
            (nil? whole-match) false
            (= unit "cm") (<= 150 (Integer/parseInt value) 193)
            (= unit "in") (<= 59 (Integer/parseInt value) 76)
            :else false
        )
    )
)

;a # followed by exactly six characters 0-9 or a-f
(defn is-hcl-valid? [x] 
    (boolean (re-matches #"#[0-9a-f]{6}" x))
)

;exactly one of: amb blu brn gry grn hzl oth
(defn is-ecl-valid? [x] 
    (contains? #{"amb" "blu" "brn" "gry" "grn" "hzl" "oth"} x)
)

;a nine-digit number, including leading zeroes
(defn is-pid-valid? [x]
    (boolean (re-matches #"[0-9]{9}" x))
)


(defn is-passport-valid-part2? [passport]
    (and 
        (and (contains? passport :byr) (is-byr-valid? (:byr passport)))
        (and (contains? passport :iyr) (is-iyr-valid? (:iyr passport)))
        (and (contains? passport :eyr) (is-eyr-valid? (:eyr passport)))
        (and (contains? passport :hgt) (is-hgt-valid? (:hgt passport)))
        (and (contains? passport :hcl) (is-hcl-valid? (:hcl passport)))
        (and (contains? passport :ecl) (is-ecl-valid? (:ecl passport)))
        (and (contains? passport :pid) (is-pid-valid? (:pid passport)))
    )  
)

(def input (slurp "input04"))

(def input-blobs (str/split input #"\n\n")) ; separated by blank line = 2 consecutive newlines

(def input-lines 
    (map
        #(str/replace % "\n" " ")
        input-blobs
    )
)

(def input-records
    (map
        get-passport-record
        input-lines
    )
)

(comment
(doseq [i (take 10 input-lines)]
    (let [rec (get-passport-record i)]
        (println (str "raw-> " i))
        (println (str "record-> " rec))
        (println (str "valid? " (is-passport-valid? rec)))
        (println)
    )
)
)

(println (str "total passports: " (count input-records)))

(println "PART 1")
(def valid-count-p1 (count (filter identity (map is-passport-valid-part1? input-records))))
(println (str "valid passports: " valid-count-p1))

; answer: 250

(println "PART 2")
(def valid-count-p2 (count (filter identity (map is-passport-valid-part2? input-records))))
(println (str "valid passports: " valid-count-p2))

; answer: 158
