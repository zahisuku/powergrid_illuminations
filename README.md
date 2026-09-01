# すべてのプラットフォームjarをビルド（fabric/build/libsとneoforge/build/libsに出力されます）
```
./gradlew build
```

特定のプラットフォーム用の開発クライアントを起動してテストしたい場合
- fabricのみの場合
```bash
./gradlew :fabric:runClient
```
- neoforgeのみの場合
```bash
./gradlew :neoforge:runClient
```
