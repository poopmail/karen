# karen

Simple incident handler for poopmail

### Report receiver configuration

Default config:
```toml
[reports]

    [reports.example1]
    type = "http"
    url = "http://example.com"
    method = "POST"
    payload = "{\"hello\": \"world\", \"field1\": \"{SERVICE_NAME} {TOPIC} {DESCRIPTION} {TYPE}\"}" # Optional
        [reports.example1.header] # Optional
        "Authorization" = "..."

    [reports.example2]
    type = "discord"
    url = "https://discord.com/api/webhooks/123/abc"
    content = "@everyone" # Optional
        [reports.example2.icons] # Optional
        "toilet" = "https://cdn.bigbathroomshop.co.uk/media/catalog/product/cache/07469e52453ec3e9e92a88c7a63b12d8/h/l/hlb031w_ls_b_new_1000.jpg"

    [reports.example3]
    type = "telegram"
    bottoken = "1234567:ABCdefgHIjklMNOpQrSTUVwXYZ"
    chatid = "1234567"
```

### Credits and thirdpary licenses

[Lettuce](https://github.com/lettuce-io/lettuce-core): \
[Apache License 2.0](https://github.com/lettuce-io/lettuce-core/blob/main/LICENSE)

[Gson](https://github.com/google/gson): \
[Apache License 2.0](https://github.com/google/gson/blob/master/LICENSE)

[toml](https://github.com/Jezza/toml): \
[MIT License](https://github.com/Jezza/toml/blob/master/LICENSE)

[discord-webhooks](https://github.com/MinnDevelopment/discord-webhooks): \
[Apache License 2.0](https://github.com/MinnDevelopment/discord-webhooks/blob/master/LICENSE)