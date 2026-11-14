# Poison PVP Optimizer - Minecraft 26.2 / Fabric

Sodium ile birlikte çalışan, istemci taraflı PvP FPS ve görüntü ayarları menüsü.

## Güvenilir derleme ve indirme

Resmî derlemeler GitHub Actions tarafından kaynak koddan oluşturulur. İndirmeden önce:
1. [Actions](https://github.com/arenkuzey8-cloud/poison-pvp-optimizer/actions) sayfasından en son başarılı **build** çalışmasını aç.
2. **Artifacts** bölümünden `poison-pvp-optimizer-verified-build` dosyasını indir.
3. Arşivdeki `SHA256SUMS.txt` ile JAR dosyasının SHA-256 özetini doğrula.
4. İsteğe bağlı olarak GitHub'ın build provenance attestation doğrulamasını kullan.

SHA-256 özeti dosyanın indirirken değişip değişmediğini kontrol etmeye yarar; tek başına zararlı yazılım taraması veya güvenlik garantisi değildir. Chrome uyarısı devam ederse dosyayı çalıştırma ve Google Safe Browsing üzerinden yanlış tespit incelemesi iste.

## Gerekli modlar

Fabric API + Sodium (26.2). Performans için ayrıca:
Lithium, FerriteCore, ImmediatelyFast, Entity Culling, More Culling, ModernFix.

Bu modları tek JAR'a birleştirmiyoruz; ayrı güncellenirler ve lisans koşullarına uymak gerekir.

## Kullanım

- Sağ Shift: menüyü aç/kapat (Controls > Poison PVP Optimizer'dan değiştirilebilir).
- Menüde bir butona tıkla: aç/kapat (yeşil = açık).
- Presetler: Maks FPS, PvP Modu, Hepsini Kapat.
