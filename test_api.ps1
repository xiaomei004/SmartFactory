Write-Host "=========================================="
Write-Host " SmartFactory Product API Test"
Write-Host "=========================================="

$base = "http://localhost:8080"
$headers = @{"Content-Type" = "application/json"}

Write-Host "`n[1/5] LIST /product/list"
$body = '{"pageNum":1,"pageSize":5,"factoryId":1}'
Invoke-RestMethod -Uri "$base/product/list" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n[2/5] DETAIL /product/detail"
$body = '{"id":1}'
Invoke-RestMethod -Uri "$base/product/detail" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n[3/5] ADD /product/add"
$body = '{"productNum":"QA001","productName":"test_product","factoryId":1}'
Invoke-RestMethod -Uri "$base/product/add" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n[4/5] EDIT /product/edit"
$body = '{"id":14,"productNum":"SMART001","productName":"modified_name"}'
Invoke-RestMethod -Uri "$base/product/edit" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n[5/5] DELETE /product/delete"
$body = '{"id":999}'
Invoke-RestMethod -Uri "$base/product/delete" -Method Post -Headers $headers -Body $body | ConvertTo-Json

Write-Host "`n=========================================="
Write-Host " Test Complete"
Write-Host "=========================================="
Read-Host "Press Enter to exit"
