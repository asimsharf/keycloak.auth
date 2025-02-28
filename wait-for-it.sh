### wait-for-it.sh
#!/usr/bin/env bash
set -e
host="$1"
shift
cmd="$@"
until nc -z "$host"; do
  >&2 echo "Waiting for $host"
  sleep 1
done
>&2 echo "$host is available"
exec $cmd
