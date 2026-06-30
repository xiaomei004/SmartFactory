Write-Host "=========================================="
Write-Host " SmartFactory API Test (Phase 1 + 2)"
Write-Host "=========================================="

$base = "http://localhost:8080"
$headers = @{"Content-Type" = "application/json"}

# ===== Product =====
Write-Host "`n===== Product ====="

Write-Host "`n[P1] LIST /product/list"
$body = '{"pageNum":1,"pageSize":5,"factoryId":1}'
Invoke-RestMethod -Uri "$base/product/list" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n[P2] DETAIL /product/detail"
$body = '{"id":1}'
Invoke-RestMethod -Uri "$base/product/detail" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n[P3] ADD /product/add"
$body = '{"productNum":"QA001","productName":"test_product","factoryId":1}'
Invoke-RestMethod -Uri "$base/product/add" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n[P4] EDIT /product/edit"
$body = '{"id":14,"productName":"modified_name"}'
Invoke-RestMethod -Uri "$base/product/edit" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n[P5] DELETE /product/delete"
$body = '{"id":999}'
Invoke-RestMethod -Uri "$base/product/delete" -Method Post -Headers $headers -Body $body | ConvertTo-Json

# ===== Equipment =====
Write-Host "`n===== Equipment ====="

Write-Host "`n[E1] LIST /equipment/list"
$body = '{"pageNum":1,"pageSize":5,"factoryId":1}'
Invoke-RestMethod -Uri "$base/equipment/list" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n[E2] DETAIL /equipment/detail"
$body = '{"id":1}'
Invoke-RestMethod -Uri "$base/equipment/detail" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n[E3] ADD /equipment/add"
$body = '{"equipmentSeq":"EQ-TEST","equipmentName":"test_eq","factoryId":1}'
Invoke-RestMethod -Uri "$base/equipment/add" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n[E4] PRODUCTS /equipment/products"
$body = '{"id":1}'
Invoke-RestMethod -Uri "$base/equipment/products" -Method Post -Headers $headers -Body $body | ConvertTo-Json

# ===== User =====
Write-Host "`n===== User ====="

Write-Host "`n[U1] LOGIN /user/login"
$body = '{"userName":"admin","userPasswd":"123456"}'
Invoke-RestMethod -Uri "$base/user/login" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n[U2] REGISTER /user/register"
$body = '{"userName":"testuser","userPasswd":"123456","factoryId":1}'
Invoke-RestMethod -Uri "$base/user/register" -Method Post -Headers $headers -Body $body | ConvertTo-Json

# ===== Factory =====
Write-Host "`n===== Factory ====="

Write-Host "`n[F1] REGISTER /factory/register"
$body = '{"factoryName":"TestFactory","factoryAddr":"Shanghai"}'
Invoke-RestMethod -Uri "$base/factory/register" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n=========================================="
Write-Host " Test Complete"
Write-Host "=========================================="
Read-Host "Press Enter to exit"
