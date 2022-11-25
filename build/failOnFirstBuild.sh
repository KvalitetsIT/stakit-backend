#!/bin/sh

echo "${GITHUB_REPOSITORY}"
echo "${DOCKER_SERVICE}"
if [ "${GITHUB_REPOSITORY}" != "KvalitetsIT/stakit-backend" ] && [ "${DOCKER_SERVICE}" = "kvalitetsit/stakit-backend" ]; then
  echo "Please run setup.sh REPOSITORY_NAME"
  exit 1
fi
