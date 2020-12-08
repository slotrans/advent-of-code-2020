; 16-18 h: hhhhhhhhhhhhhhhhhh
; 17-18 d: ddddddddddddddddzn
; 15-18 c: cccccccccccccczcczc
; 3-9 r: pplzctdrc
; 4-14 d: lxdmddfddddddd

(defn record-from-line [line]
    (let [ [rule-part password] (str/split line #": ")
         , [range-part letter] (str/split rule-part #" ")
         , [lower-bound upper-bound] (str/split range-part #"-")
         ]
         { :password password
         , :letter (first (seq letter)) ; convert to character type
         , :lower-bound (Integer/parseInt lower-bound)
         , :upper-bound (Integer/parseInt upper-bound)
         }
    )
)

(def input-records
    (map
        record-from-line
        (str/split (slurp "input02") #"\n")
    )    
)

(defn satisfies-p1-rule? [rec]
    (let [ {:keys [password letter lower-bound upper-bound]} rec
         , occurrences (count (filter #(= letter %) (seq password)))
         ]
         ;(println (str letter " occurs " occurrences " times in '" password "'"))
         (<= lower-bound occurrences upper-bound)
    )
)

(println "part 1")
(println
    (count
        (filter 
            identity ; removes false-y things
            (map
                #(satisfies-p1-rule? %)
                input-records
            )
        )
    )
)

(defn satisfies-p2-rule? [rec]
    (let [ {:keys [password letter lower-bound upper-bound]} rec
         , first-index (- lower-bound 1)
         , second-index (- upper-bound 1)
         , at-first-position (= letter (get password first-index))
         , at-second-position (= letter (get password second-index))
         ]
         (not (= at-first-position at-second-position)) ; XOR
    )    
)

(println "part 2")
(println
    (count
        (filter 
            identity ; removes false-y things
            (map
                #(satisfies-p2-rule? %)
                input-records
            )
        )
    )
)
