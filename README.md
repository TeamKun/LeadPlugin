# LeadPlugin
手つなぎプラグイン  
リードで対象と一心同体、離れなれない！  

# 動作条件  
- Minecraft 1.15.2
- PaperMC 1.15.2

# 仕様  
- リードを持って対象を右クリックして捕縛  
- 再度対象をリードを持って右クリックして解放  
- 素手で左クリックして手繰る  

# 依存プラグイン  
- [LeadWires](https://www.spigotmc.org/resources/leadwires.76515/)  
- [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)

# コンフィグ(lead configコマンド)
- holder_power  
  捕縛者の手繰る力　　
- target_power  
  被捕縛者の手繰る力
- force_pull_power  
  最大距離まで移動した際の引き戻す力  
- max_distance  
  移動可能な距離  
- force_teleport_distance  
  強制的にテレポートされる距離  
- lead_after_death  
  パートナーの死後、リスポーンした際に再度束縛するかどうか  
- lead_only_player  
  プラグインの有効範囲をプレイヤーに限定するかどうか
- particle_mode  
  リードの代わりにパーティクルを表示かどうか
- particle_type  
  パーティクルのタイプ
  
# コマンド  
- lead config  
  コンフィグの設定
