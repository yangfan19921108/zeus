# 疑难杂症
- consul 依赖 actuator, 否则 Service Checks 会有一把大大而又红红的叉叉, 逼死强迫症的那种.
- consul 读取配置需要配置在 bootstrap.yml, application.yml 会读取错误.
- gateway 404 异常, spring.cloud.gateway.routes 加上 filters: - StripPrefix=1 即可