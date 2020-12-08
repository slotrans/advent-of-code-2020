-- eyr:2024 pid:662406624 hcl:#cfa07d byr:1947 iyr:2015 ecl:amb hgt:150cm
-- 
-- iyr:2013 byr:1997 hgt:182cm hcl:#ceb3a1
-- eyr:2027
-- ecl:gry cid:102 pid:018128535
-- 
-- hgt:61in iyr:2014 pid:916315544 hcl:#733820 ecl:oth
-- 
-- hcl:#a97842
-- eyr:2026 byr:1980 ecl:grn pid:726519569 hgt:184cm cid:132 iyr:2011

--The expected fields are as follows:
--    byr (Birth Year)
--    iyr (Issue Year)
--    eyr (Expiration Year)
--    hgt (Height)
--    hcl (Hair Color)
--    ecl (Eye Color)
--    pid (Passport ID)
--    cid (Country ID)   <-- optional


--PART 2 VALIDATIONS

-- byr (Birth Year) - four digits; at least 1920 and at most 2002.
-- iyr (Issue Year) - four digits; at least 2010 and at most 2020.
-- eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
-- hgt (Height) - a number followed by either cm or in:
-- 
--     If cm, the number must be at least 150 and at most 193.
--     If in, the number must be at least 59 and at most 76.
-- 
-- hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
-- ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
-- pid (Passport ID) - a nine-digit number, including leading zeroes.

/*
=# create table advent2020.input04 ( idx serial primary key, val text ) ;

=# \copy input04 (val) from 'input04' ;

=# select * from input04 order by idx limit 10 ;
 idx |                                  val
-----+------------------------------------------------------------------------
   1 | eyr:2024 pid:662406624 hcl:#cfa07d byr:1947 iyr:2015 ecl:amb hgt:150cm
   2 |
   3 | iyr:2013 byr:1997 hgt:182cm hcl:#ceb3a1
   4 | eyr:2027
   5 | ecl:gry cid:102 pid:018128535
   6 |
   7 | hgt:61in iyr:2014 pid:916315544 hcl:#733820 ecl:oth
   8 |
   9 | hcl:#a97842
  10 | eyr:2026 byr:1980 ecl:grn pid:726519569 hgt:184cm cid:132 iyr:2011
*/

with marked as
(
    select i.idx
         , i.val
         , (select min(j.idx)
              from input04 j
             where i.idx < j.idx
               and j.val = ''
           ) as NEXT_BLANK_LINE
      from input04 i
     where 1=1
)
, assembled as
(
    select next_blank_line
         , min(idx) as MIN_IDX
         , string_agg(val, ' ') as FULL_VAL
      from marked
     where 1=1
       and val != ''
     group by next_blank_line
     order by next_blank_line nulls last
)
, part1_answer as
(
    --most direct route to answer
    select 'part 1'
         , count(1)
      from assembled 
     where 1=1
       and full_val like '%byr:%'
       and full_val like '%iyr:%'
       and full_val like '%eyr:%'
       and full_val like '%hgt:%'
       and full_val like '%hcl:%'
       and full_val like '%ecl:%'
       and full_val like '%pid:%'
    --answer: 250
)
, subfields as 
(
    select min_idx
         , full_val
         , case when full_val like '%byr:%'
                then (regexp_match(full_val, '(byr)(:)([^[:space:]]+)'))[3]
                 end as BYR
         , case when full_val like '%iyr:%'
                then (regexp_match(full_val, '(iyr)(:)([^[:space:]]+)'))[3]
                 end as IYR
         , case when full_val like '%eyr:%'
                then (regexp_match(full_val, '(eyr)(:)([^[:space:]]+)'))[3]
                 end as EYR
         , case when full_val like '%hgt:%'
                then (regexp_match(full_val, '(hgt)(:)([^[:space:]]+)'))[3]
                 end as HGT
         , case when full_val like '%hcl:%'
                then (regexp_match(full_val, '(hcl)(:)([^[:space:]]+)'))[3]
                 end as HCL
         , case when full_val like '%ecl:%'
                then (regexp_match(full_val, '(ecl)(:)([^[:space:]]+)'))[3]
                 end as ECL
         , case when full_val like '%pid:%'         
                then (regexp_match(full_val, '(pid)(:)([^[:space:]]+)'))[3]
                 end as PID
         , case when full_val like '%cid:%'         
                then (regexp_match(full_val, '(cid)(:)([^[:space:]]+)'))[3]
                 end as CID
      from assembled
     where 1=1
     order by min_idx                 
)
, parsed as
(
    select min_idx
         , case when regexp_match(byr, '^[0-9]{4}$') is not null
                then byr::int
                 end as BYR
         , case when regexp_match(iyr, '^[0-9]{4}$') is not null
                then iyr::int
                 end as IYR
         , case when regexp_match(eyr, '^[0-9]{4}$') is not null
                then eyr::int
                 end as EYR
         , case when regexp_match(hgt, '^([0-9]+)(cm|in)$') is not null
                then regexp_match(hgt, '^([0-9]+)(cm|in)$')
                 end as HGT
         , case when regexp_match(hcl, '^#[0-9a-f]{6}$') is not null
                then hcl
                 end as HCL
         , case when ecl in ('amb', 'blu', 'brn', 'gry', 'grn', 'hzl', 'oth')
                then ecl
                 end as ECL
         , case when regexp_match(pid, '^[0-9]{9}$') is not null
                then pid
                 end as PID
      from subfields 
     where 1=1
)
, validations as
(
    select min_idx
         --
         , byr
         , (byr::int between 1920 and 2002) as BYR_VALID
         --
         , iyr
         , (iyr::int between 2010 and 2020) as IYR_VALID
         --
         , eyr
         , (eyr::int between 2020 and 2030) as EYR_VALID
         --
         , hgt
         , (    hgt is not null
            and (   (hgt[2] = 'cm' and hgt[1]::int between 150 and 193)
                 or (hgt[2] = 'in' and hgt[1]::int between 59 and 76)
                )
           ) as HGT_VALID
         --
         , hcl
         , (hcl is not null) as HCL_VALID
         --
         , ecl
         , (ecl is not null) as ECL_VALID
         --
         , pid
         , (pid is not null) as PID_VALID
      from parsed
     where 1=1   
)
, part2_answer as
(
    select 'part 2'
         , count(1)
      from validations
     where 1=1
       and byr_valid
       and iyr_valid
       and eyr_valid
       and hgt_valid
       and hcl_valid
       and ecl_valid
       and pid_valid
    --answer: 158
)
select * from part1_answer
union all
select * from part2_answer
;
