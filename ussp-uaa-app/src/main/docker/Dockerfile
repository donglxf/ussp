# 鍩虹闀滃儚  
FROM frolvlad/alpine-oraclejdk8:slim

#璁剧疆鏃跺尯
ENV TZ "Asia/Shanghai"

# VOLUME鍛戒护鐢ㄤ簬璁╀綘鐨勫鍣ㄨ闂涓绘満涓婄殑鐩綍銆� 
#VOLUME "/tmp"
  
# ADD鍛戒护鏈変袱涓弬鏁帮紝婧愬拰鐩爣銆傚畠鐨勫熀鏈綔鐢ㄦ槸浠庢簮绯荤粺鐨勬枃浠剁郴缁熶笂澶嶅埗鏂囦欢鍒扮洰鏍囧鍣ㄧ殑鏂囦欢绯荤粺銆傚鏋滄簮鏄竴涓猆RL锛岄偅璇RL鐨勫唴瀹瑰皢琚笅杞藉苟澶嶅埗鍒板鍣ㄤ腑銆�  
ADD ussp-uaa-app.jar app.jar
  
# RUN鍛戒护鏄疍ockerfile鎵ц鍛戒护鐨勬牳蹇冮儴鍒嗐�傚畠鎺ュ彈鍛戒护浣滀负鍙傛暟骞剁敤浜庡垱寤洪暅鍍忋��  
RUN sh -c 'touch /app.jar'

#ENV鍛戒护鐢ㄤ簬璁剧疆鐜鍙橀噺銆傝繖浜涘彉閲忎互鈥漦ey=value鈥濈殑褰㈠紡瀛樺湪锛屽苟鍙互鍦ㄥ鍣ㄥ唴琚剼鏈垨鑰呯▼搴忚皟鐢ㄣ��  
ENV JAVA_OPTS="-Xms512M -Xmx512M -Xss256K  -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45"

# ENTRYPOINT 甯姪浣犻厤缃竴涓鍣ㄤ娇涔嬪彲鎵ц鍖�  
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
