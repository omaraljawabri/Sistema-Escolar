#!/usr/bin/env bash
set -euo pipefail

: "${RENDER_DEPLOY_HOOK_URL:?RENDER_DEPLOY_HOOK_URL não definido}"
: "${APP_URL:?APP_URL não definido}"
: "${IMAGE:?IMAGE não definido}"
: "${COMMIT:?COMMIT não definido}"
TIMEOUT_SEGUNDOS="${TIMEOUT_SEGUNDOS:-900}"

imagem_codificada=$(jq -rn --arg valor "$IMAGE" '$valor | @uri')
echo "Solicitando deploy da imagem $IMAGE"
curl -fsS -X POST "${RENDER_DEPLOY_HOOK_URL}&imgURL=${imagem_codificada}" > /dev/null

echo "Aguardando $APP_URL responder com o commit $COMMIT"
limite=$((SECONDS + TIMEOUT_SEGUNDOS))
while true; do
  commit_atual=$(curl -fsS --max-time 15 "${APP_URL}/actuator/info" 2>/dev/null | jq -r '.app.commit // empty' || true)
  if [[ "$commit_atual" == "$COMMIT" ]]; then
    break
  fi
  if (( SECONDS >= limite )); then
    echo "::error::A versão $COMMIT não ficou disponível em $APP_URL após ${TIMEOUT_SEGUNDOS}s (versão atual: ${commit_atual:-indisponível})" >&2
    exit 1
  fi
  echo "Versão atual: ${commit_atual:-indisponível}. Nova tentativa em 15s..."
  sleep 15
done

curl -fsS --max-time 15 "${APP_URL}/actuator/health/readiness" | jq -e '.status == "UP"' > /dev/null
echo "Deploy concluído: $APP_URL está rodando o commit $COMMIT"
