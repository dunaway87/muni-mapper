Select ST_ASTEXT(my_geom) from "Parcels" where ST_CONTAINS(my_geom, ST_SETSRID(ST_POINT(?,?),4326)) limit 1
