; ....#.#..#.#.#.#......#....##.#
; ..##..#.#..#.##.....#.....#....
; ....#..#...#..#..####.##.#.##..
; ...............#.....##..##..#.
; ##...####..##.#..#...####...#.#
; ..#.#....##....##.........#...#
; .#..#.##..............#.....###
; ##..##..#.....#..#...#....#....
; .#.........#..#...#.#.#.....#..
; ......#...#..#.##..#.....#.#...
; .#...#.#.#.##.##.....###...#...
; ..........#.......#...#....#..#
; .....##..#.#...#...##.##.......
; ...#.###.#.#..##...#.#.........
; ###.###....#...###.#.##...#....
; ...........#....#.....##....###
; #..#.......#.....#.....##....#.
; .##.#....#...#....#......#..##.
; ..#....#..#..#......#..........
; #..#.........#.#....#.##...#.#.

(ns net.blergh.advent2020
    (:require [clojure.string :as str])
)

(comment
(def sled-space
    (to-array-2d
        (map
            seq
            (str/split (slurp "input03") #"\n")
        )
    )
)
) ; arrays are nice for indexing but inconvenient for everything else

(def sled-space
    (vec
        (map
            vec
            (vec (str/split (slurp "input03") #"\n"))
        )
    )
)

(defn index-by-coords [space x y]
    (let [row (get space (- y 1))]
        (get row (- x 1))
    )
)

(defn brussel-wrap [x]
    ;(+ (mod (- x 1) 31) 1) ; this is only necessary when using 1-based indexing
    (mod x 31)
)

(def x-slope 1)
(def y-slope 2)


(defn go-sledding [hill xpos ypos tree-count]
    (if (>= ypos (count hill))
        tree-count
        (let [ terrain-row (get hill ypos)
             , terrain-cell (get terrain-row xpos)
             , tree-value (if (= \# terrain-cell) 1 0)
             , pretty-terrain-row (str/join "" (assoc terrain-row xpos (if (= tree-value 1) \X \O)))
             ]
            (println (str pretty-terrain-row " | " terrain-cell " at (" xpos "," ypos ") -> " tree-value))
            (go-sledding
                hill
                (brussel-wrap (+ xpos x-slope))
                (+ ypos y-slope)
                (+ tree-count tree-value)
            )
        )   
    )
)

(def trees-hit (go-sledding sled-space 0 0 0))
(println (str "trees hit: " trees-hit))

; works but does not show the rows that we didn't land on
; if we didn't print as we went, but passed a mutated hill to the next recursion,
; we could print it at the end

; The basic trade-off seems to be that if we use cursor movement or recursive consumption to move through the hill,
; then the Y-axis movement is accomplished through the how we move the cursor, or how we recurse, while the X-axis
; movement is simple indexing. Implementing each axis in a different way is somewhat inelegant. Also in this model, 
; we have to jump through hoops if we want to output the rows that we skip when the Y slope is >1 (though printing
; is not a requirement of the puzzle).
;
; Whereas if we use indexing for both axes of movement... I dunno maybe it's just better. We give up natural
; iterator-style movement through the data, but that's not a big deal. When the Y slope is >1 we never consume the
; skipped rows, so we have a similar printing problem as with cursor/recursion, but that can be solved by mutating
; the hill and printing at the end. One difficulty with that though is that you must return the hill, so finding
; the tree count requires a post-processing step. That in turn could be avoided by passing a more bigger data
; structure between recursions.
