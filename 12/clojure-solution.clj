;The navigation instructions (your puzzle input) consists of a sequence of single-character 
; actions paired with integer input values. After staring at them for a few minutes, you work out what they probably mean:
;
;    Action N means to move north by the given value.
;    Action S means to move south by the given value.
;    Action E means to move east by the given value.
;    Action W means to move west by the given value.
;    Action L means to turn left the given number of degrees.
;    Action R means to turn right the given number of degrees.
;    Action F means to move forward by the given value in the direction the ship is currently facing.
;
;The ship starts by facing east. Only the L and R actions change the direction the ship is facing. 
;(That is, if the ship is facing east and the next instruction is N10, the ship would move north 10 units, 
;but would still move east if the following action were F.)
;
;For example:
;
;F10
;N3
;F7
;R90
;F11
;
;These instructions would be handled as follows:
;
;    F10 would move the ship 10 units east (because the ship starts by facing east) to east 10, north 0.
;    N3 would move the ship 3 units north to east 10, north 3.
;    F7 would move the ship another 7 units east (because the ship is still facing east) to east 17, north 3.
;    R90 would cause the ship to turn right by 90 degrees and face south; it remains at east 17, north 3.
;    F11 would move the ship 11 units south to east 17, south 8.
;
;At the end of these instructions, the ship's Manhattan distance (sum of the absolute values of its east/west 
;position and its north/south position) from its starting position is 17 + 8 = 25.
;
;Figure out where the navigation instructions lead. What is the Manhattan distance between that location and the 
;ship's starting position?

(ns net.blergh.advent2020
    (:require [clojure.string :as str]
    )
)

(def sample-instructions 
    [[:F 10] [:N 3] [:F 7] [:R 90] [:F 11]]
)

; let North = 0 degrees, and positive rotation be clockwise

(def deg-map {:N 0, :E 90, :S 180, :W 270})

(defn get-heading [direction]
    (direction deg-map)
)

(defn rotate [heading degrees]
    (mod (+ degrees heading) 360)
)

(defn move [coords heading distance]
    (let [[x y] coords]
        (cond
            (= heading   0) [x (+ y distance)]
            (= heading  90) [(+ x distance) y]
            (= heading 180) [x (- y distance)]
            (= heading 270) [(- x distance) y]
        )
    )
)

(defn navigate [instructions coords heading]
    (let [ instruction (first instructions)
         , [inst arg] instruction
         ]
        ;(println (str "at (" (first coords) "," (second coords) ") facing " heading))
        ;(println (str "> " instruction))
        (if (nil? instruction)
            coords
            (cond
                (contains? #{:N :S :E :W} inst)
                    (navigate (rest instructions) (move coords (get-heading inst) arg) heading)
                (= :L inst)
                    (navigate (rest instructions) coords (rotate heading (* -1 arg)))
                (= :R inst)
                    (navigate (rest instructions) coords (rotate heading arg))
                (= :F inst)
                    (navigate (rest instructions) (move coords heading arg) heading)
            )
        )
    )
)

(defn manhattan-distance [coords]
    (let [[x y] coords]
        (+ (Math/abs x) (Math/abs y))
    )
)

;(println (manhattan-distance (navigate sample-instructions [0 0] 90)))

(def input-instructions
    (for [line (str/split-lines (slurp "input12"))]
        (let [ letter (keyword (subs line 0 1))
             , number (Integer/parseInt (subs line 1))
             ]
            [letter number]
        )
    )
)

(def part1-answer (manhattan-distance (navigate input-instructions [0 0] 90)))
(println (str "part1 answer: " part1-answer))
;answer=820
