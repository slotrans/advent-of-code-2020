;For example, consider the following rules:
;
;light red bags contain 1 bright white bag, 2 muted yellow bags.
;dark orange bags contain 3 bright white bags, 4 muted yellow bags.
;bright white bags contain 1 shiny gold bag.
;muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
;shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
;dark olive bags contain 3 faded blue bags, 4 dotted black bags.
;vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
;faded blue bags contain no other bags.
;dotted black bags contain no other bags.
;
;These rules specify the required contents for 9 bag types. 
;In this example, every faded blue bag is empty, 
;every vibrant plum bag contains 11 bags (5 faded blue and 6 dotted black), and so on.
;
;You have a shiny gold bag. If you wanted to carry it in at least one other bag, 
;how many different bag colors would be valid for the outermost bag? 
;(In other words: how many colors can, eventually, contain at least one shiny gold bag?)


(ns net.blergh.advent2020
    (:require [clojure.string :as str]
    )
)

(def input07 (slurp "input07"))

(def input-lines (str/split-lines input07))

; "faded blue" -> :faded-blue
(defn kebab-keyword [s]
    (keyword (str/replace s " " "-"))
)

; for each value in input collection:
;   "5 dull orange bags" -> {:dull-orange 5}
(defn get-inner-rules [exprs]
    (into
        {}
        (for [e exprs]
            (let [ stripped (str/replace e #" bag(s)?" "")
                 , [whole-match count color] (re-matches #"([0-9]+)\s([a-z\s]+)" stripped)
                 ]
                {(kebab-keyword color) (Integer/parseInt count)}
            )
        )
    )
)

(defn rule-from-line [line]
    (let [ line (str/replace line "." "") ; strip trailing period
         , [outer-part inner-part] (str/split line #" bags contain ")
         , outer-color (kebab-keyword outer-part)
         ]
        (if (= inner-part "no other bags")
            {outer-color {}}
            (let [content-strings (str/split inner-part #", ")]
                {outer-color (get-inner-rules content-strings)}
            )
        )
    )
)


(def input-rules
    (into
        {}
        (for [line input-lines]
            (rule-from-line line)
        )
    )
)

(println (str "count of rules: " (count input-rules)))
;(println input-rules)

;manually written out
(def sample-input-parsed
    { :light-red {:bright-white 1 
                  :muted-yellow 2}

    , :dark-orange {:bright-white 3
                    :muted-yellow 4}

    , :bright-white {:shiny-gold 1}

    , :muted-yellow {:shiny-gold 2
                     :faded-blue 9}

    , :shiny-gold {:dark-olive 1
                   :vibrant-plum 2}

    , :dark-olive {:faded-blue 3
                   :dotted-black 4}

    , :vibrant-plum {:faded-blue 5
                     :dotted-black 6}

    , :faded-blue {}

    , :dotted-black {}
    }
)


(defn is-bag-anywhere-inside [target-color search-color rules]
    (let [contents (search-color rules)]
        (if (empty? contents)
            false
            (if (contains? (set (keys contents)) target-color)
                true
                (some #(is-bag-anywhere-inside target-color % rules) (keys contents))
            )
        )
    )
)

(doseq [k (keys sample-input-parsed)]
    (println (str k " -> " (is-bag-anywhere-inside :shiny-gold k sample-input-parsed)))
)


(def part1-answer
    (count 
        (filter 
            identity 
            (for [color-rule input-rules]
                (is-bag-anywhere-inside :shiny-gold (key color-rule) input-rules)
            )
        )
    )
)
(println (str "outer bags that can contain a :shiny-gold = " part1-answer))
;answer=101

;Consider again your shiny gold bag and the rules from the above example:
;
;    faded blue bags contain 0 other bags.
;    dotted black bags contain 0 other bags.
;    vibrant plum bags contain 11 other bags: 5 faded blue bags and 6 dotted black bags.
;    dark olive bags contain 7 other bags: 3 faded blue bags and 4 dotted black bags.
;
;So, a single shiny gold bag must contain 1 dark olive bag (and the 7 bags within it) 
;plus 2 vibrant plum bags (and the 11 bags within each of those): 1 + 1*7 + 2 + 2*11 = 32 bags!
;
;How many individual bags are required inside your single shiny gold bag?

(defn sum-bags-inside [root-color rules]
    (apply
        +
        (for [sub-rule (root-color rules)]
            (let [ color (key sub-rule)
                 , count (val sub-rule)
                 ]
                (+ count(* count (sum-bags-inside color rules)))
            )
        )
    )
)

(def part2-answer
    (sum-bags-inside :shiny-gold input-rules)
)
(println (str "total bags inside :shiny-gold = " part2-answer))
;answer=108636
