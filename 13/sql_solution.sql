

with var as
(
    select (1024*1024) as RANGE_MAX
         , 1000677 as EARLIEST_DEPARTURE
         --, 939 as EARLIEST_DEPARTURE
)
, sample_input13 as
(
    select  7 as BUS_7
         , 13 as BUS_13
         , 59 as BUS_59
         , 31 as BUS_31
         , 19 as BUS_19
)
, input13 as
(
    select  29 as BUS_29
         ,  41 as BUS_41
         , 661 as BUS_661
         ,  13 as BUS_13
         ,  17 as BUS_17
         ,  23 as BUS_23
         , 521 as BUS_521
         ,  37 as BUS_37
         ,  19 as BUS_19
)
, input as
(
    select * from input13
)
, part1_base as
(
    select ser.val
         --
         --, case when mod(val, bus_7) = 0 then bus_7 end as BUS_7_ARRIVAL
         --, case when mod(val, bus_13) = 0 then bus_13 end as BUS_13_ARRIVAL
         --, case when mod(val, bus_19) = 0 then bus_19 end as BUS_19_ARRIVAL
         --, case when mod(val, bus_31) = 0 then bus_31 end as BUS_31_ARRIVAL
         --, case when mod(val, bus_59) = 0 then bus_59 end as BUS_59_ARRIVAL
         --
         , case when mod(val, bus_13) = 0 then bus_13 end as BUS_13_ARRIVAL
         , case when mod(val, bus_17) = 0 then bus_17 end as BUS_17_ARRIVAL
         , case when mod(val, bus_19) = 0 then bus_19 end as BUS_19_ARRIVAL
         , case when mod(val, bus_23) = 0 then bus_23 end as BUS_23_ARRIVAL
         , case when mod(val, bus_29) = 0 then bus_29 end as BUS_29_ARRIVAL
         , case when mod(val, bus_37) = 0 then bus_37 end as BUS_37_ARRIVAL
         , case when mod(val, bus_41) = 0 then bus_41 end as BUS_41_ARRIVAL
         , case when mod(val, bus_521) = 0 then bus_521 end as BUS_521_ARRIVAL
         , case when mod(val, bus_661) = 0 then bus_661 end as BUS_661_ARRIVAL
      from generate_series(1, (select range_max from var)) as ser(val)
      join input on(true)
     where 1=1
)
, part1_phase2 as
(
    select min(val) as MIN_VAL
      from part1_base
     where 1=1
       and val >= (select earliest_departure from var)
       --and coalesce(bus_7_arrival, bus_13_arrival, bus_59_arrival, bus_31_arrival, bus_19_arrival) is not null
       and coalesce( bus_13_arrival
                   , bus_17_arrival
                   , bus_19_arrival
                   , bus_23_arrival
                   , bus_29_arrival
                   , bus_37_arrival
                   , bus_41_arrival
                   , bus_521_arrival
                   , bus_661_arrival
                   ) is not null
)
, part1_answer as
(
    select *
      from part1_base
     where 1=1
       and val = (select min_val from part1_phase2)
)
select * from part1_answer
;

select (1000684 - 1000677) * 23 ; --answer: 161


--part2 using sample
/*
select ser.val
  from generate_series(1, 1024*1024*2) as ser(val)
 where 1=1
   and mod(ser.val + 0,  7) = 0
   and mod(ser.val + 1, 13) = 0
   and mod(ser.val + 4, 59) = 0
   and mod(ser.val + 6, 31) = 0
   and mod(ser.val + 7, 19) = 0
 limit 1
;
*/


/*
29 --@0
--18 minute gap
41 --@19
--9 minute gap
661 --@29
--12 minute gap
13 --@42
17 --@43
--8 minute gap
23 --@52
--7 minute gap
521 --@60
--5 minute gap
37 --@66
--12 minute gap
19 --@79
*/


--part2 using input
/*
select ser.val
  from generate_series( 100000000000000 --100 trillion
                      , (2^62)::bigint --if it's larger than that we're in trouble...
                      ) as ser(val)
 where 1=1
   and mod(ser.val +  0,  29) = 0
   and mod(ser.val + 19,  41) = 0
   and mod(ser.val + 29, 661) = 0
   and mod(ser.val + 42,  13) = 0
   and mod(ser.val + 43,  17) = 0
   and mod(ser.val + 52,  23) = 0
   and mod(ser.val + 60, 521) = 0
   and mod(ser.val + 66,  37) = 0
   and mod(ser.val + 79,  19) = 0
 limit 1
;
*/

/*
select ser.val - 30
  from generate_series( 100000000000000-312 + (1::bigint * 100 * 1000 * 1000 * 1000) --100 trillion
                      , 100000000000000 + (2::bigint * 100 * 1000 * 1000 * 1000)
                      , 661
                      ) as ser(val)
 where 1=1
   and mod(ser.val - 29,  29) = 0
   and mod(ser.val -  9,  41) = 0
   and mod(ser.val +  0, 661) = 0
   and mod(ser.val + 12,  13) = 0
   and mod(ser.val + 13,  17) = 0
   and mod(ser.val + 22,  23) = 0
   and mod(ser.val + 30, 521) = 0
   and mod(ser.val + 36,  37) = 0
   and mod(ser.val + 49,  19) = 0
 order by ser.val
 limit 1
;
*/

do $$
declare
    n_search_start bigint := 100000000000000 - 312 ; --mod(this, 661) = 0
    n_increment bigint := (1000 * 1000 * 1000) + 460 ;
    n_result bigint := null ;
begin
    loop
        raise notice 'searching from %', n_search_start ;

        select ser.val - 30
          into n_result
          from generate_series( n_search_start
                              , n_search_start + (n_increment - 1)
                              , 661
                              ) as ser(val)
         where 1=1
           and mod(ser.val - 29,  29) = 0
           and mod(ser.val -  9,  41) = 0
           and mod(ser.val +  0, 661) = 0
           and mod(ser.val + 12,  13) = 0
           and mod(ser.val + 13,  17) = 0
           and mod(ser.val + 22,  23) = 0
           and mod(ser.val + 30, 521) = 0
           and mod(ser.val + 36,  37) = 0
           and mod(ser.val + 49,  19) = 0
         order by ser.val
         limit 1
        ;

        exit when n_result is not null ;

        n_search_start := n_search_start + n_increment ;
    end loop; 
    raise notice 'result %', n_result ;
end;
$$ language plpgsql;


/*
--67,7,x,59,61
select ser.val - 4
  from generate_series( 0
                      , (2^30)::bigint
                      , 61
                      ) as ser(val)
 where 1=1
   and mod(ser.val + 0 - 4, 67) = 0
   and mod(ser.val + 1 - 4,  7) = 0
   and mod(ser.val + 3 - 4, 59) = 0
   and mod(ser.val + 4 - 4, 61) = 0
 order by ser.val
 limit 1
;
*/

with sample_input13(offset, bus_id) as
(
    values (0,  7)
         , (1, 13)
         , (4, 59)
         , (6, 31)
         , (7, 19)
)
, input as
(
    select offset::bigint as OFFSET
         , bus_id::bigint as BUS_ID
      from sample_input13
     where 1=1
     order by offset
)
, idk as 
(
    with recursive periodized_search(_time, period, next_offset, next_bus_id) as
    (
        select _time
             , period
             , _time + period as NEXT_TIME
             , next_offset
             , next_bus_id
          from (
                select offset as _time
                     , bus_id as period
                     , lead(offset,1) over(order by offset) as NEXT_OFFSET
                     , lead(bus_id,1) over(order by offset) as NEXT_BUS_ID
                  from input
                 where 1=1
               ) first_bus
         where 1=1
        --
        union all
        --
        select ps.next_time as _TIME
             , 
             , case when 
          from periodized_search ps
          join (
                select _time + period as _TIME
                  from periodized_search
                 where 1=1
               ) x
         where 1=1
           and 
    )
)

;