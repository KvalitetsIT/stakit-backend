#!/bin/bash

if docker pull kvalitetsit/stakit-backend-documentation:latest; then
    echo "Copy from old documentation image."
    docker cp $(docker create kvalitetsit/stakit-backend-documentation:latest):/usr/share/nginx/html target/old
fi
