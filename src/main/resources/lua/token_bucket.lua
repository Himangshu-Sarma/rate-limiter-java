-- KEYS[1]: Token count key, KEYS[2]: Last refill timestamp key
-- ARGV[1]: Capacity, ARGV[2]: Refill rate (tokens per sec), ARGV[3]: Current timestamp (epoch seconds), ARGV[4]: Requested tokens

local token_key = KEYS[1]
local timestamp_key = KEYS[2]

local capacity = tonumber(ARGV[1])
local reffil_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

-- Get current tokens and last refill time
local current_tokens = tonumber(redis.call('get', token_key))
local last_refill = tonumber(redis.call('get', timestamp_key))

if current_tokens = nil then
    current_tokens = capacity
    last_refill = now
else
    -- Calculate elapsed time and generated tokens
    local delta = math.max(0, now - last_refill)
    local tokens_to_add = delta * refill_rate
    current_tokens = math.min(capacity, current_tokens + tokens_to_add)
    last_refill = now
end

-- Check if enough tokens are available
if current_tokens >= requested then
    current_tokens = current_tokens - requested
    redis.call('set', token_key, current_tokens)
    redis.call('set', timestamp_key, last_refill)
    return 1 -- Allowed
else
    -- Still update timestamp even if denied to prevent floating point drift
    redis.call('set', token_key, current_tokens)
    redis.call('set', timestamp_key, last_refill)
    return 0 -- Denied
end
