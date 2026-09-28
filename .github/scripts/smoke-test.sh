#!/usr/bin/env bash
set -euo pipefail

: "${APP_URL:?APP_URL não definido}"

email="smoke-$(date +%s)-${RANDOM}@example.com"
senha="smoke-${RANDOM}${RANDOM}"
readonly FORMATO_STATUS='%{http_code}'

verificar_status() {
  local descricao="$1" esperado="$2" obtido="$3"
  if [[ "$obtido" != "$esperado" ]]; then
    echo "::error::$descricao: esperado HTTP $esperado, obtido HTTP $obtido" >&2
    exit 1
  fi
  echo "OK - $descricao (HTTP $obtido)"
}

status=$(curl -s -o /dev/null -w "$FORMATO_STATUS" "${APP_URL}/actuator/health/liveness")
verificar_status "Liveness" 200 "$status"

status=$(curl -s -o /dev/null -w "$FORMATO_STATUS" -X POST -H 'Content-Type: application/json' \
  -d "{\"email\":\"${email}\",\"senha\":\"${senha}\",\"nome\":\"Smoke\",\"sobrenome\":\"Test\",\"role\":\"ESTUDANTE\"}" \
  "${APP_URL}/api/v1/auth/registrar")
verificar_status "Cadastro de estudante" 200 "$status"

token=$(curl -fsS -X POST -H 'Content-Type: application/json' \
  -d "{\"email\":\"${email}\",\"senha\":\"${senha}\"}" \
  "${APP_URL}/api/v1/auth/login" | jq -r '.token // empty')
if [[ -z "$token" ]]; then
  echo "::error::Login não retornou token" >&2
  exit 1
fi
echo "OK - Login retornou token JWT"

status=$(curl -s -o /dev/null -w "$FORMATO_STATUS" "${APP_URL}/api/v1/estatisticas/estudante")
verificar_status "Endpoint protegido sem token" 403 "$status"

status=$(curl -s -o /dev/null -w "$FORMATO_STATUS" -H "Authorization: Bearer ${token}" "${APP_URL}/api/v1/estatisticas/estudante")
verificar_status "Endpoint protegido com token" 200 "$status"

echo "Smoke test concluído com sucesso"
