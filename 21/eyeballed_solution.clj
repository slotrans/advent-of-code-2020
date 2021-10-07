;; This is a scratch file I used to work out the solution by hand from the set intersections.
;; Once I had this done, I was sure my approach would work, and coding it was just a matter
;; of thinking through the mental steps I followed.

user=> input-allergens
#{"eggs" "sesame" "peanuts" "wheat" "shellfish" "soy" "nuts" "fish"}


user=> (apply set/intersection (map :ingredients (filter #(contains? (:allergens %) "shellfish") parsed-input21)))
#{"jvgnc"}

user=> (apply set/intersection (map :ingredients (filter #(contains? (:allergens %) "nuts") parsed-input21)))
#{"jvgnc" "lpzgzmk"}

user=> (apply set/intersection (map :ingredients (filter #(contains? (:allergens %) "sesame") parsed-input21)))
#{"stj" "lpzgzmk"}

user=> (apply set/intersection (map :ingredients (filter #(contains? (:allergens %) "eggs") parsed-input21)))
#{"kbmlt" "stj"}

user=> (apply set/intersection (map :ingredients (filter #(contains? (:allergens %) "soy") parsed-input21)))
#{"gxnr" "kbmlt" "lpzgzmk"}

user=> (apply set/intersection (map :ingredients (filter #(contains? (:allergens %) "wheat") parsed-input21)))
#{"gxnr" "plrlg" "lpzgzmk"}

user=> (apply set/intersection (map :ingredients (filter #(contains? (:allergens %) "fish") parsed-input21)))
#{"kbmlt" "plrlg" "mrccxm" "lpzgzmk"}

;;;;;;;;;;;;;;;

user=> (apply set/intersection (map :ingredients (filter #(contains? (:allergens %) "peanuts") parsed-input21)))
#{"ppj" "mrccxm" "lpzgzmk"}





{ "shellfish" "jvgnc"
, "nuts"      "lpzgzmk"
, "sesame"    "stj"
, "eggs"      "kbmlt"
, "soy"       "gxnr"
, "wheat"     "plrlg"
, "fish"      "mrccxm"
, "peanuts"   "ppj"
}
