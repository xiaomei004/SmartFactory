@echo off
echo ==========================================
echo  SmartFactory Product API Test
echo ==========================================

echo.
echo [1/5] LIST /product/list
curl -s -X POST http://localhost:8080/product/list -H "Content-Type: application/json" -d "{\"pageNum\":1,\"pageSize\":5,\"factoryId\":1}"
echo.
echo.

echo [2/5] DETAIL /product/detail
curl -s -X POST http://localhost:8080/product/detail -H "Content-Type: application/json" -d "{\"id\":1}"
echo.
echo.

echo [3/5] ADD /product/add
curl -s -X POST http://localhost:8080/product/add -H "Content-Type: application/json" -d "{\"productNum\":\"QA001\",\"productName\":\"test\",\"factoryId\":1}"
echo.
echo.

echo [4/5] EDIT /product/edit
curl -s -X POST http://localhost:8080/product/edit -H "Content-Type: application/json" -d "{\"id\":14,\"productName\":\"newname\"}"
echo.
echo.

echo [5/5] DELETE /product/delete
curl -s -X POST http://localhost:8080/product/delete -H "Content-Type: application/json" -d "{\"id\":999}"
echo.
echo.

echo ==========================================
echo  Test Complete
echo ==========================================
pause
