

with var as
(
    select (1024*1024) as RANGE_MAX
         --, 1000677 as EARLIEST_DEPARTURE
         , 939 as EARLIEST_DEPARTURE
)
--, blergh as
--(
--29
--41
--661
--13
--17
--23
--521
--37
--19
--)
, sample as
(
    select  7 as bus_7
         , 13 as bus_13
         , 59 as bus_59
         , 31 as bus_31
         , 19 as bus_19
)
, part1_base as
(
select ser.val
     , case when mod(val, bus_7) = 0 then bus_7 end as BUS_7_ARRIVAL
     , case when mod(val, bus_13) = 0 then bus_13 end as BUS_13_ARRIVAL
     , case when mod(val, bus_59) = 0 then bus_59 end as BUS_59_ARRIVAL
     , case when mod(val, bus_31) = 0 then bus_31 end as BUS_31_ARRIVAL
     , case when mod(val, bus_19) = 0 then bus_19 end as BUS_19_ARRIVAL
  from generate_series(1, (select range_max from var)) as ser(val)
  join sample on(true)
 where 1=1
)
, part1_answer as
(
    select 'part1 answer'
         , min(val)
      from part1_base
     where 1=1
       and val >= (select earliest_departure from var)
       and coalesce(bus_7_arrival, bus_13_arrival, bus_59_arrival, bus_31_arrival, bus_19_arrival) is not null
)
select * from part1_answer
;  