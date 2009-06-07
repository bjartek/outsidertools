#!/bin/bash

mvn scala:run "-DaddArgs=$1.dnd4e|$1.html"
scp $1.html pvv:web-docs/ot/
