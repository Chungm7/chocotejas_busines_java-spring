# 🚀 INICIO RÁPIDO - Sistema de Chocotejas con Node.js

## ⚡ Ejecución en 3 Pasos

### 1️⃣ Abrir PowerShell
```powershell
cd "C:\Users\chung\OneDrive - Universidad Tecnologica del Peru\UTP\Ciclo VI\Marco de Desarrollo Web\chocotejas-bussiness"
```

### 2️⃣ Ejecutar Script de Inicio
```powershell
.\start-clean.ps1
```

### 3️⃣ Probar los Servicios
```powershell
.\test-node-services.ps1
```

---

## 🌐 URLs de Acceso

| Servicio | URL |
|----------|-----|
| **Dashboard Principal** | http://localhost:8083/gestion/dashboard-node |
| **Aplicación Web** | http://localhost:8083 |
| **Node Service 1** | http://localhost:3001/api/data |
| **Node Service 2** | http://localhost:3002/api/data |
| **Node Service 3** | http://localhost:3003/api/data |
| **phpMyAdmin** | http://localhost:8081 |

---

## 🛠️ Comandos Rápidos

```powershell
# Ver estado
docker-compose ps

# Ver logs
docker-compose logs -f

# Reiniciar
docker-compose restart

# Detener
docker-compose down

# Probar servicios
.\test-node-services.ps1
```

---

## 📚 Documentación

- **Guía Completa**: `GUIA_INTEGRACION_NODE.md`
- **Resumen**: `RESUMEN_IMPLEMENTACION_NODE.md`

---

## ✅ Verificación Rápida

Los servicios están funcionando si ves:

```
✓ node-service-1    Up
✓ node-service-2    Up
✓ node-service-3    Up
✓ sistema-chocotejas_container    Up
✓ db_mysql_container_chocotejas   Up
```

---

## 🆘 Solución Rápida de Problemas

### Si algo falla:

```powershell
# Limpia todo
docker-compose down
docker system prune -f

# Vuelve a ejecutar
.\start-clean.ps1
```

---

**¡Listo para usar! 🎉**

