---
--- 接口限流脚本
---
--- Created by wangzhengjin.
--- DateTime: 2022/12/15 22:13
---

-- 方法名
local methodKey = KEYS[1]
-- 方法限流上限值
local limit = tonumber(ARGV[1])
-- 方法当前请求量
local curr = tonumber(redis.call('get', methodKey) or "0")
if curr + 1 > limit then
    -- 达上线，拒绝访问
    return false
else
    -- 访问量增加
    redis.call("INCRBY", methodKey, 1)
    redis.call("EXPIRE", methodKey, 1)
    return true
end