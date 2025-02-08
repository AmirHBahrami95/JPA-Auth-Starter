tkn=$(node -e "const auth=require('./logged_in.json'); console.log(auth.token);")
curl "http://localhost:8080/api/user/whoami" \
-H "Authorization: Bearer ${tkn}" \
-i
echo
