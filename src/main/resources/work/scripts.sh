#!/usr/bin/env bash
set -euo pipefail

CODEQL="$HOME/tools/codeql/codeql"   # <-- adjust if folder name differs
DOT="dot"
GIT="git"

# sanity
"$CODEQL" version
command -v "$DOT" >/dev/null 2>&1 || { echo "ERROR: graphviz 'dot' not found in PATH"; exit 127; }
command -v "$GIT" >/dev/null 2>&1 || { echo "ERROR: git not found in PATH"; exit 127; }

if [[ $# -lt 3 ]]; then
  echo "Usage: $0 <repoUrl> <cloneDir> <language> [queriesDir] [workDir]"
  echo "Example: $0 https://github.com/org/repo.git /src/main/resources/clone java /src/main/resources/queries /database"
  exit 2
fi

REPO_URL="$1"
CLONE_DIR="$2"
LANGUAGE="$3"
QUERIES_DIR="${4:-./queries}"   # folder containing .ql files
WORK_DIR="${5:-$(pwd)}"

# Derived paths
DB_DIR="${WORK_DIR}/codeql-db"
OUT_DIR="${WORK_DIR}/out"

if [[ ! -d "${QUERIES_DIR}" ]]; then
  echo "ERROR: queriesDir not found: ${QUERIES_DIR}"
  exit 2
fi

echo "==> Repo:      ${REPO_URL}"
echo "==> Clone dir: ${CLONE_DIR}"
echo "==> Language:  ${LANGUAGE}"
echo "==> Queries:   ${QUERIES_DIR}"
echo "==> Work dir:  ${WORK_DIR}"
echo

# Clean work outputs
rm -rf "${DB_DIR}" "${OUT_DIR}"
mkdir -p "${WORK_DIR}" "${OUT_DIR}"

# Clone
echo "==> Cloning repository..."
rm -rf "${CLONE_DIR}"
git clone --depth 1 "${REPO_URL}" "${CLONE_DIR}"

# Create CodeQL database
echo "==> Creating CodeQL database..."
# For Java-only repos, build-mode=none is fastest. Switcex
$CODEQL database create "${DB_DIR}" \
  --language="${LANGUAGE}" \
  --source-root="${CLONE_DIR}" \
  --build-mode=none

# Run queries (compute results)
echo "==> Running CodeQL queries..."
# You can point QUERIES_DIR to a folder with .ql files or a .qls suite.
$CODEQL database run-queries "${DB_DIR}" "${QUERIES_DIR}"

# Interpret results to DOT + render SVG
echo "==> Interpreting results to DOT and rendering SVG..."
shopt -s nullglob
QL_FILES=("${QUERIES_DIR}"/*.ql)

if (( ${#QL_FILES[@]} == 0 )); then
  echo "WARNING: No .ql files found in ${QUERIES_DIR}. If you use a .qls suite, pass it as queriesDir."
fi

for q in "${QL_FILES[@]}"; do
  base="$(basename "${q}" .ql)"
  dot_out="${OUT_DIR}/${base}.dot"
  svg_out="${OUT_DIR}/${base}.svg"

  echo "   - ${base}"

  # DOT output works only for queries with:
  #   /** @kind graph */
  $CODEQL database interpret-results \
    --format=dot \
    --output="${OUT_DIR}" \
    -- "${DB_DIR}" "${q}"

  # interpret-results may output with a generated name; normalize to base.dot if possible
  # If exactly one new .dot appears, rename it.
  newest_dot="$(ls -t "${OUT_DIR}"/*.dot 2>/dev/null | head -n 1 || true)"
  if [[ -n "${newest_dot}" && "${newest_dot}" != "${dot_out}" ]]; then
    mv -f "${newest_dot}" "${dot_out}"
  fi

  if [[ -f "${dot_out}" ]]; then
    dot -Tsvg "${dot_out}" -o "${svg_out}"
  else
    echo "     WARNING: No DOT produced for ${q}. Ensure it has /** @kind graph */"
  fi
done

echo
echo "==> Done."
echo "Artifacts in: ${OUT_DIR}"
ls -la "${OUT_DIR}" || true
