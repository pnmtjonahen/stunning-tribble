#!/bin/sh
cf delete ptj-movie-front -r -f
cf delete ptj-movie-movies  -r -f
cf delete ptj-movie-review  -r -f
cf delete ptj-movie-watchlist -r -f
cf delete ptj-movie-gateway -r -f

