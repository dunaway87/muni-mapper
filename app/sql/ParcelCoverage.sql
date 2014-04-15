SELECT (
round(
(
(
ST_AREA(
ST_INTERSECTION(
(Select my_geom from "Parcels" where "Parcel_Number" = '$parcelNumber$'),
my_geom)
)
)/(
Select ST_AREA(my_geom) from "Parcels" where "Parcel_Number" = '$parcelNumber$'
)
)::numeric,3)*100)
from "$layerName$" where ST_AREA(ST_INTERSECTION(
(Select my_geom from "Parcels" where "Parcel_Number" = '$parcelNumber$'),
my_geom)) >0

