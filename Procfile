web: echo '#!/bin/sh
if [ -z "$PGHOST" ] && [ -n "$DATABASE_URL" ]; then
  url=${DATABASE_URL#*://}
  user_pass=${url%@*}
  host_port_db=${url##*@}
  export PGUSER=${user_pass%:*}
  export PGPASSWORD=${user_pass#*:}
  export PGDATABASE=${host_port_db#*/}
  host_port=${host_port_db%/*}
  export PGHOST=${host_port%:*}
  export PGPORT=${host_port#*:}
fi
export SPRING_DATASOURCE_URL="jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}"
export SPRING_DATASOURCE_USERNAME="${PGUSER}"
export SPRING_DATASOURCE_PASSWORD="${PGPASSWORD}"
echo "Starting with DB URL: $SPRING_DATASOURCE_URL"
java -Dspring.profiles.active=railway -jar target/backend.inventory-0.0.1-SNAPSHOT.jar
' > run.sh && sh run.sh