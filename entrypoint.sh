#!/bin/sh
set -e

# If PGHOST is missing, try to parse DATABASE_URL
if [ -z "$PGHOST" ] && [ -n "$DATABASE_URL" ]; then
  echo "PGHOST not set, parsing DATABASE_URL..."
  # Strip schema
  url_no_schema=${DATABASE_URL#*://}
  
  # Extract user:pass (everything before the LAST @)
  user_pass=${url_no_schema%@*}
  export PGUSER=${user_pass%:*}
  export PGPASSWORD=${user_pass#*:}
  
  # Extract host:port/db (everything after the LAST @)
  host_port_db=${url_no_schema##*@}
  
  # Extract db
  export PGDATABASE=${host_port_db#*/}
  
  # Extract host:port
  host_port=${host_port_db%/*}
  export PGHOST=${host_port%:*}
  export PGPORT=${host_port#*:}
  
  echo "Parsed host: $PGHOST, port: $PGPORT"
fi

# Set Spring Datasource variables explicitly
# This overrides application.yml settings
export SPRING_DATASOURCE_URL="jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}"
export SPRING_DATASOURCE_USERNAME="${PGUSER}"
export SPRING_DATASOURCE_PASSWORD="${PGPASSWORD}"

echo "Starting application with JDBC URL: $SPRING_DATASOURCE_URL"

exec java -Dspring.profiles.active=railway -jar target/backend.inventory-0.0.1-SNAPSHOT.jar