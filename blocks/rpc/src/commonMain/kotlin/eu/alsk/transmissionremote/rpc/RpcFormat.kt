package eu.alsk.transmissionremote.rpc

/**
 * Selects the wire format of the RPC requests. See section 1 of
 * `docs/reference/transmission-rpc-api.md`.
 */
public enum class RpcFormat {
    /** Transmission 4.1 and later: JSON-RPC 2.0 with `snake_case` names. */
    JsonRpc2,

    /** Transmission 4.0 and earlier: the legacy format with `kebab-case` and `camelCase` names. */
    Legacy,
}
