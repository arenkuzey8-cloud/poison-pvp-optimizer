# Poison PVP Optimizer - Minecraft 26.2 / Fabric

Sodium ile birlikte çalışan PvP odaklı FPS menüsü.

## Derleme
1. JDK 25
2. Gradle 9.5.1+ (veya Fabric example mod'dan wrapper dosyalarını kopyala)
3. `gradle build` -> `build/libs/poison-pvp-optimizer-1.0.0.jar`

## Mods klasörü
Fabric API + Sodium (26.2). FPS için asıl kazanç bu modlardan gelir, bu mod sadece ayarları tek menüden yönetir:
Lithium, FerriteCore, ImmediatelyFast, Entity Culling, More Culling, ModernFix.
(Bu modları tek jar'a birleştirmiyoruz: lisansları buna izin vermiyor, ayrıca her biri ayrı güncelleniyor.)

## FPS hakkında
2000 FPS garanti edilemez; işlemci, ekran kartı ve sunucu yoğunluğuna bağlı. Maks FPS preset'i + yukarıdaki modlar
güçlü bir PC'de en iyi sonucu verir.

## Kullanım
- Sağ Shift: menüyü aç/kapat (Controls > Poison PVP Optimizer'dan değiştirilebilir)
- Menüde bir butona tıkla: aç/kapat (yeşil = açık)
- "Zoom Tuşu" butonuna tıkla, sonra atamak istediğin tuşa bas (ESC iptal, DEL tuşu siler)
- Zoom tuşu basılı tutulunca çalışır; "Zoom FOV" 30/40/50 arasında döner
- Presetler: Maks FPS, PvP Modu, Hepsini Kapat
